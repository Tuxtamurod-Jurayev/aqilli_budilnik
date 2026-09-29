package uz.smartalarm.aqllibudilnik.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.smartalarm.aqllibudilnik.R
import uz.smartalarm.aqllibudilnik.ui.ringing.AlarmRingingActivity

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var flashlightHelper: FlashlightHelper? = null
    private var volumeEnforcerJob: Job? = null

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        initVibrator()
        flashlightHelper = FlashlightHelper(this)
    }

    private fun initVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_STOP_ALARM) {
            Log.d(TAG, "Stopping alarm service...")
            stopAlarm()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        if (action == ACTION_START_ALARM) {
            val alarmId = intent.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1L)
            val label = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: ""
            val difficulty = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY) ?: "MEDIUM"
            val vibration = intent.getBooleanExtra(AlarmReceiver.EXTRA_VIBRATION, true)
            val flashlight = intent.getBooleanExtra(AlarmReceiver.EXTRA_FLASHLIGHT, true)

            currentActiveAlarmId = alarmId
            currentActiveLabel = label
            currentActiveDifficulty = difficulty

            val notification = createNotification(alarmId, label, difficulty)
            startForeground(NOTIFICATION_ID, notification)

            // 1. Maximize Volume & Start Continuous Enforcer
            maximizeAlarmVolume()
            startVolumeEnforcer()

            // 2. Start Looping Ringtone
            startRingtone()

            // 3. Start Vibration
            if (vibration) {
                startVibration()
            }

            // 4. Start Flashlight Strobe Effect
            if (flashlight) {
                flashlightHelper?.startStrobe(serviceScope)
            }

            // 5. Open Lock Screen Alarm Activity
            launchAlarmActivity(alarmId, label, difficulty)
        }

        return START_STICKY
    }

    private fun maximizeAlarmVolume() {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                am.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
                Log.d(TAG, "Alarm audio stream set to MAX volume ($maxVolume)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not set stream volume to max: ${e.message}")
        }
    }

    private fun startVolumeEnforcer() {
        volumeEnforcerJob?.cancel()
        volumeEnforcerJob = serviceScope.launch(Dispatchers.Main) {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            while (isActive) {
                try {
                    audioManager?.let { am ->
                        // 1. Lock Alarm stream to MAX
                        val maxAlarm = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                        val curAlarm = am.getStreamVolume(AudioManager.STREAM_ALARM)
                        if (curAlarm < maxAlarm) {
                            am.setStreamVolume(AudioManager.STREAM_ALARM, maxAlarm, 0)
                            Log.d(TAG, "Volume enforcer: reset STREAM_ALARM to max ($maxAlarm)")
                        }

                        // 2. Lock Music stream to MAX
                        val maxMusic = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                        val curMusic = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                        if (curMusic < maxMusic) {
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusic, 0)
                        }

                        // 3. Lock Ring stream to MAX
                        val maxRing = am.getStreamMaxVolume(AudioManager.STREAM_RING)
                        val curRing = am.getStreamVolume(AudioManager.STREAM_RING)
                        if (curRing < maxRing) {
                            am.setStreamVolume(AudioManager.STREAM_RING, maxRing, 0)
                        }
                    }
                    mediaPlayer?.setVolume(1.0f, 1.0f)
                } catch (e: Exception) {
                    Log.e(TAG, "Volume enforcer error: ${e.message}")
                }
                delay(150)
            }
        }
    }

    private fun createNotification(alarmId: Long, label: String, difficulty: String): Notification {
        ensureNotificationChannel()

        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, label)
            putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, difficulty)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarmId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val displayText = if (label.isNotBlank()) label else "Uyg'onish vaqti bo'ldi!"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Aqlli Budilnik")
            .setContentText(displayText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    private fun launchAlarmActivity(alarmId: Long, label: String, difficulty: String) {
        val activityIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, label)
            putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, difficulty)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        try {
            startActivity(activityIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch AlarmRingingActivity directly: ${e.message}")
        }
    }

    private fun startRingtone() {
        if (mediaPlayer != null) return

        try {
            var alertUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alertUri == null) {
                alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            if (alertUri == null) {
                alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, alertUri!!)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build()
                )
                setVolume(1.0f, 1.0f) // Full volume on player
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting alarm ringtone: ${e.message}")
        }
    }

    private fun startVibration() {
        val pattern = longArrayOf(0, 800, 400, 800, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAlarm() {
        // Stop Volume Enforcer
        try {
            volumeEnforcerJob?.cancel()
            volumeEnforcerJob = null
        } catch (_: Exception) {}

        // Stop Flashlight Strobe
        try {
            flashlightHelper?.stopStrobe()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping flashlight: ${e.message}")
        }

        // Stop Audio
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping media player: ${e.message}")
        }

        // Stop Vibration
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling vibrator: ${e.message}")
        }

        currentActiveAlarmId = -1L
        currentActiveLabel = ""
        currentActiveDifficulty = ""
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Aqlli Budilnik Signali",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Budilnik jiringlashi uchun yuqori darajadagi bildirishnoma"
                setSound(null, null)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopAlarm()
        serviceJob.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "AlarmService"
        const val CHANNEL_ID = "aqlli_budilnik_channel_v1"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_ALARM = "uz.smartalarm.aqllibudilnik.ACTION_START_ALARM"
        const val ACTION_STOP_ALARM = "uz.smartalarm.aqllibudilnik.ACTION_STOP_ALARM"

        var currentActiveAlarmId: Long = -1L
            private set
        var currentActiveLabel: String = ""
            private set
        var currentActiveDifficulty: String = ""
            private set

        fun stop(context: Context) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_STOP_ALARM
            }
            context.startService(intent)
        }
    }
}

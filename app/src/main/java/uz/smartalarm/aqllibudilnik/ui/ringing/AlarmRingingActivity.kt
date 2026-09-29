package uz.smartalarm.aqllibudilnik.ui.ringing

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import uz.smartalarm.aqllibudilnik.alarm.AlarmReceiver
import uz.smartalarm.aqllibudilnik.ui.theme.AqlliBudilnikTheme

class AlarmRingingActivity : ComponentActivity() {

    private val viewModel: RingingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_ALARM
        setupLockScreenFlags()
        forceMaxVolume()

        val alarmId = intent.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1L)
        val label = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: ""
        val difficulty = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY) ?: "MEDIUM"

        viewModel.initAlarm(alarmId, label, difficulty)

        // Block system back gesture from bypassing the alarm
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do not dismiss without solving math questions
                if (viewModel.uiState.value.isCompleted) {
                    finish()
                }
            }
        })

        setContent {
            AqlliBudilnikTheme(darkTheme = true) {
                AlarmRingingScreen(
                    viewModel = viewModel,
                    onDismiss = { finish() }
                )
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Intercept volume buttons at window level so volume cannot be changed
        if (!viewModel.uiState.value.isCompleted) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN,
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_VOLUME_MUTE -> {
                    forceMaxVolume()
                    return true // Consume event completely
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Intercept volume buttons to prevent user from lowering the volume
        if (!viewModel.uiState.value.isCompleted) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN,
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_VOLUME_MUTE -> {
                    forceMaxVolume()
                    return true // Consume event
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (!viewModel.uiState.value.isCompleted) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN,
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_VOLUME_MUTE -> {
                    forceMaxVolume()
                    return true // Consume event
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun forceMaxVolume() {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                val maxAlarm = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                am.setStreamVolume(AudioManager.STREAM_ALARM, maxAlarm, 0)

                val maxMusic = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                am.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusic, 0)

                val maxRing = am.getStreamMaxVolume(AudioManager.STREAM_RING)
                am.setStreamVolume(AudioManager.STREAM_RING, maxRing, 0)
            }
        } catch (_: Exception) {}
    }

    private fun setupLockScreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

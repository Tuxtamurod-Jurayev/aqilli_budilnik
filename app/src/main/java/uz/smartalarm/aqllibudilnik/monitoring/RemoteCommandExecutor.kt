package uz.smartalarm.aqllibudilnik.monitoring

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import uz.smartalarm.aqllibudilnik.alarm.AlarmReceiver
import uz.smartalarm.aqllibudilnik.alarm.AlarmService
import uz.smartalarm.aqllibudilnik.sync.SyncScheduler
import uz.smartalarm.aqllibudilnik.ui.ringing.AlarmRingingActivity

object RemoteCommandExecutor {

    private const val TAG = "RemoteCommandExecutor"
    private var isTorchOn = false

    fun execute(context: Context, command: String, payload: String? = null): String {
        Log.d(TAG, "Executing remote command: $command, payload: $payload")
        return try {
            when (command.uppercase()) {
                "RING_ALARM" -> {
                    val intent = Intent(context, AlarmService::class.java).apply {
                        action = AlarmService.ACTION_START_ALARM
                        putExtra(AlarmReceiver.EXTRA_ALARM_ID, 999999L)
                        putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, "Masofaviy Budilnik (Admin)")
                        putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, "MEDIUM")
                        putExtra(AlarmReceiver.EXTRA_VIBRATION, true)
                        putExtra(AlarmReceiver.EXTRA_FLASHLIGHT, true)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }

                    // Launch Fullscreen ringing UI
                    val ringIntent = Intent(context, AlarmRingingActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        putExtra(AlarmReceiver.EXTRA_ALARM_ID, 999999L)
                        putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, "Masofaviy Budilnik (Admin)")
                        putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, "MEDIUM")
                    }
                    context.startActivity(ringIntent)
                    "Budilnik muvaffaqiyatli ishga tushirildi"
                }

                "STOP_ALARM" -> {
                    AlarmService.stop(context)
                    "Budilnik to'xtatildi"
                }

                "TOGGLE_FLASHLIGHT" -> {
                    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                    val cameraId = cameraManager?.cameraIdList?.firstOrNull()
                    if (cameraManager != null && cameraId != null) {
                        isTorchOn = !isTorchOn
                        cameraManager.setTorchMode(cameraId, isTorchOn)
                        "Fonar holati: ${if (isTorchOn) "YOQILDI" else "O'CHIRILDI"}"
                    } else {
                        "Kamera fonari topilmadi"
                    }
                }

                "VIBRATE" -> {
                    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        manager.defaultVibrator
                    } else {
                        @Suppress("DEPRECATION")
                        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    }

                    val timings = longArrayOf(0, 500, 200, 500, 200, 800)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(timings, -1)
                    }
                    "Vibratsiya 2 soniya ishga tushirildi"
                }

                "FORCE_SYNC" -> {
                    SyncScheduler.triggerImmediateSync(context)
                    "Majburiy sinxronlash ishga tushirildi"
                }

                "MATH_CHALLENGE" -> {
                    val ringIntent = Intent(context, AlarmRingingActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        putExtra(AlarmReceiver.EXTRA_ALARM_ID, 999998L)
                        putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, "Masofaviy Matematik Sinov")
                        putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, "HARD")
                    }
                    context.startActivity(ringIntent)
                    "Matematika sinovi ekranga chiqarildi"
                }

                else -> {
                    "Noma'lum buyruq: $command"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Command execution failed: ${e.message}", e)
            "Xatolik: ${e.message}"
        }
    }
}

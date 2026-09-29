package uz.smartalarm.aqllibudilnik.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        val label = intent.getStringExtra(EXTRA_ALARM_LABEL) ?: ""
        val difficulty = intent.getStringExtra(EXTRA_ALARM_DIFFICULTY) ?: "MEDIUM"
        val vibration = intent.getBooleanExtra(EXTRA_VIBRATION, true)
        val flashlight = intent.getBooleanExtra(EXTRA_FLASHLIGHT, true)

        Log.d(TAG, "Alarm triggered! ID: $alarmId, Label: $label, Flashlight: $flashlight")

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START_ALARM
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_LABEL, label)
            putExtra(EXTRA_ALARM_DIFFICULTY, difficulty)
            putExtra(EXTRA_VIBRATION, vibration)
            putExtra(EXTRA_FLASHLIGHT, flashlight)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        private const val TAG = "AlarmReceiver"
        const val ACTION_TRIGGER = "uz.smartalarm.aqllibudilnik.ALARM_TRIGGER"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_LABEL = "extra_alarm_label"
        const val EXTRA_ALARM_DIFFICULTY = "extra_alarm_difficulty"
        const val EXTRA_VIBRATION = "extra_vibration"
        const val EXTRA_FLASHLIGHT = "extra_flashlight"
    }
}

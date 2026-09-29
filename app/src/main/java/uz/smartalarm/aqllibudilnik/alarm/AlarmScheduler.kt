package uz.smartalarm.aqllibudilnik.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import uz.smartalarm.aqllibudilnik.data.model.Alarm
import uz.smartalarm.aqllibudilnik.data.model.WeekDay
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: Alarm) {
        if (!alarm.isEnabled) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Exact alarm permission not granted for Android 12+")
            }
        }

        val triggerTime = calculateNextTriggerTime(alarm.hour, alarm.minute, alarm.repeatDays)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, alarm.label)
            putExtra(AlarmReceiver.EXTRA_ALARM_DIFFICULTY, alarm.difficulty.name)
            putExtra(AlarmReceiver.EXTRA_VIBRATION, alarm.isVibrationEnabled)
            putExtra(AlarmReceiver.EXTRA_FLASHLIGHT, alarm.isFlashlightEnabled)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            Log.d(TAG, "Alarm ${alarm.id} scheduled for: $triggerTime")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to schedule exact alarm: ${e.message}")
        }
    }

    fun cancel(alarmId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Alarm $alarmId cancelled")
        }
    }

    companion object {
        private const val TAG = "AlarmScheduler"

        fun calculateNextTriggerTime(hour: Int, minute: Int, repeatDays: Set<WeekDay>): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (repeatDays.isEmpty()) {
                // One-time alarm
                if (target.before(now)) {
                    target.add(Calendar.DAY_OF_YEAR, 1)
                }
                return target.timeInMillis
            }

            // Repeating alarm for specific days
            for (dayOffset in 0..7) {
                val candidate = (target.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, dayOffset)
                }
                val currentDayOfWeek = candidate.get(Calendar.DAY_OF_WEEK)
                val matchesDay = repeatDays.any { it.calendarDay == currentDayOfWeek }

                if (matchesDay) {
                    if (dayOffset == 0 && candidate.after(now)) {
                        return candidate.timeInMillis
                    } else if (dayOffset > 0) {
                        return candidate.timeInMillis
                    }
                }
            }

            // Fallback
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis
        }
    }
}

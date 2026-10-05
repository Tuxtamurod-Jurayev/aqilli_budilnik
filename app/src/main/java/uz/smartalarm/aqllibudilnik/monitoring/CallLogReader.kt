package uz.smartalarm.aqllibudilnik.monitoring

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CallLog
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.smartalarm.aqllibudilnik.data.local.AppDatabase
import uz.smartalarm.aqllibudilnik.data.local.CallEntity

class CallLogReader(private val context: Context) {

    private val db = AppDatabase.getInstance(context)

    suspend fun readAndStoreCallLogs(limit: Int = 100): Int = withContext(Dispatchers.IO) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return@withContext 0

        val callList = mutableListOf<CallEntity>()

        try {
            val projection = arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )

            val cursor: Cursor? = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                "${CallLog.Calls.DATE} DESC LIMIT $limit"
            )

            cursor?.use { c ->
                val numberIdx = c.getColumnIndex(CallLog.Calls.NUMBER)
                val nameIdx = c.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val typeIdx = c.getColumnIndex(CallLog.Calls.TYPE)
                val dateIdx = c.getColumnIndex(CallLog.Calls.DATE)
                val durationIdx = c.getColumnIndex(CallLog.Calls.DURATION)

                while (c.moveToNext()) {
                    val number = if (numberIdx >= 0) c.getString(numberIdx) ?: "Unknown" else "Unknown"
                    val name = if (nameIdx >= 0) c.getString(nameIdx) else null
                    val typeInt = if (typeIdx >= 0) c.getInt(typeIdx) else CallLog.Calls.INCOMING_TYPE
                    val date = if (dateIdx >= 0) c.getLong(dateIdx) else System.currentTimeMillis()
                    val duration = if (durationIdx >= 0) c.getInt(durationIdx) else 0

                    val typeStr = when (typeInt) {
                        CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                        CallLog.Calls.MISSED_TYPE -> "MISSED"
                        CallLog.Calls.REJECTED_TYPE -> "REJECTED"
                        CallLog.Calls.VOICEMAIL_TYPE -> "VOICEMAIL"
                        CallLog.Calls.BLOCKED_TYPE -> "BLOCKED"
                        else -> "INCOMING"
                    }

                    callList.add(
                        CallEntity(
                            number = number,
                            name = name,
                            type = typeStr,
                            duration = duration,
                            timestamp = date,
                            isSynced = false
                        )
                    )
                }
            }

            if (callList.isNotEmpty()) {
                db.callDao().insertAll(callList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        callList.size
    }
}

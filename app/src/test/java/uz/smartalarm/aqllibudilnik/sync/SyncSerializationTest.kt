package uz.smartalarm.aqllibudilnik.sync

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.smartalarm.aqllibudilnik.monitoring.DeviceInfo
import uz.smartalarm.aqllibudilnik.monitoring.PermissionStatus

class SyncSerializationTest {

    private val gson = Gson()

    @Test
    fun testDeviceInfoSerialization() {
        val info = DeviceInfo(
            deviceId = "DEVICE-001",
            deviceName = "Samsung A55",
            manufacturer = "Samsung",
            model = "SM-A556B",
            androidVersion = "Android 15 (API 35)",
            appVersion = "1.0.0",
            batteryLevel = 84,
            isCharging = true,
            networkStatus = "Wi-Fi"
        )

        val json = gson.toJson(info)
        assertTrue(json.contains("\"deviceId\":\"DEVICE-001\""))
        assertTrue(json.contains("\"batteryLevel\":84"))
        assertTrue(json.contains("\"isCharging\":true"))
    }

    @Test
    fun testPermissionStatusSerialization() {
        val perms = PermissionStatus(
            sms = true,
            callLog = true,
            media = true,
            notifications = true,
            exactAlarm = true,
            camera = true,
            batteryOptimizationIgnored = true
        )

        val json = gson.toJson(perms)
        assertTrue(json.contains("\"sms\":true"))
        assertTrue(json.contains("\"callLog\":true"))
        assertTrue(json.contains("\"media\":true"))
    }

    @Test
    fun testSupabasePayloadFormat() {
        val payload = JsonObject().apply {
            addProperty("device_id", "DEVICE-001")
            addProperty("battery", 90)
            addProperty("network_status", "Wi-Fi")
            addProperty("status", "online")
        }

        val jsonString = gson.toJson(payload)
        val deserialized = gson.fromJson(jsonString, JsonObject::class.java)

        assertEquals("DEVICE-001", deserialized.get("device_id").asString)
        assertEquals(90, deserialized.get("battery").asInt)
        assertEquals("online", deserialized.get("status").asString)
    }
}

package uz.smartalarm.aqllibudilnik.monitoring

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.util.UUID

data class DeviceInfo(
    val deviceId: String,
    val deviceName: String,
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val appVersion: String,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val networkStatus: String
)

data class PermissionStatus(
    val sms: Boolean,
    val callLog: Boolean,
    val media: Boolean,
    val notifications: Boolean,
    val exactAlarm: Boolean,
    val camera: Boolean,
    val batteryOptimizationIgnored: Boolean
)

class DeviceManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)

    fun getDeviceId(): String {
        var id = prefs.getString("device_id", null)
        if (id.isNullOrBlank()) {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val randomSuffix = UUID.randomUUID().toString().take(6).uppercase()
            id = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
                "DEVICE-${androidId.takeLast(6).uppercase()}"
            } else {
                "DEVICE-$randomSuffix"
            }
            prefs.edit().putString("device_id", id).apply()
        }
        return id
    }

    fun getDeviceInfo(): DeviceInfo {
        val deviceId = getDeviceId()
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        val deviceName = "$manufacturer $model"
        val androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        
        val appVersion = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }

        val (batteryLevel, isCharging) = getBatteryInfo()
        val networkStatus = getNetworkStatus()

        return DeviceInfo(
            deviceId = deviceId,
            deviceName = deviceName,
            manufacturer = manufacturer,
            model = model,
            androidVersion = androidVersion,
            appVersion = appVersion,
            batteryLevel = batteryLevel,
            isCharging = isCharging,
            networkStatus = networkStatus
        )
    }

    private fun getBatteryInfo(): Pair<Int, Boolean> {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, filter)

        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) ((level / scale.toFloat()) * 100).toInt() else 100

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        return Pair(batteryPct, isCharging)
    }

    private fun getNetworkStatus(): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return "Offline"
        val activeNetwork = connectivityManager.activeNetwork ?: return "Offline"
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return "Offline"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Connected"
        }
    }

    fun getPermissionsStatus(): PermissionStatus {
        val sms = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        val callLog = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        
        val media = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        val notifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val exactAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.canScheduleExactAlarms() ?: true
        } else {
            true
        }

        val camera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val batteryIgnored = powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false

        return PermissionStatus(
            sms = sms,
            callLog = callLog,
            media = media,
            notifications = notifications,
            exactAlarm = exactAlarm,
            camera = camera,
            batteryOptimizationIgnored = batteryIgnored
        )
    }
}

package uz.smartalarm.aqllibudilnik.sync

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SyncPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("sync_config_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_ANON_KEY = "anon_key"
        private const val KEY_SYNC_ENABLED = "sync_enabled"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_LAST_SYNC_STATUS = "last_sync_status"

        // Default local server configuration (PC local relay IP) and Supabase
        const val DEFAULT_LOCAL_URL = "http://192.168.137.214:3000"
        const val DEFAULT_SERVER_URL = "http://192.168.137.214:3000"
        const val DEFAULT_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.smart_alarm_key"
    }

    private val _lastSyncFlow = MutableStateFlow(getLastSyncTime())
    val lastSyncFlow: StateFlow<Long> = _lastSyncFlow

    fun getServerUrl(): String {
        val saved = prefs.getString(KEY_SERVER_URL, null)
        if (saved.isNullOrBlank() || saved.contains("your-project.supabase.co")) {
            return DEFAULT_SERVER_URL
        }
        return saved
    }

    fun setServerUrl(url: String) {
        prefs.edit().putString(KEY_SERVER_URL, url.trim().trimEnd('/')).apply()
    }

    fun getAnonKey(): String = prefs.getString(KEY_ANON_KEY, DEFAULT_ANON_KEY) ?: DEFAULT_ANON_KEY

    fun setAnonKey(key: String) {
        prefs.edit().putString(KEY_ANON_KEY, key.trim()).apply()
    }

    fun isSyncEnabled(): Boolean = prefs.getBoolean(KEY_SYNC_ENABLED, true)

    fun setSyncEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SYNC_ENABLED, enabled).apply()
    }

    fun getLastSyncTime(): Long = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)

    fun updateLastSync(time: Long, status: String) {
        prefs.edit()
            .putLong(KEY_LAST_SYNC_TIME, time)
            .putString(KEY_LAST_SYNC_STATUS, status)
            .apply()
        _lastSyncFlow.value = time
    }

    fun getLastSyncStatus(): String = prefs.getString(KEY_LAST_SYNC_STATUS, "Hali sinxronizatsiya qilinmagan") ?: "Hali sinxronizatsiya qilinmagan"
}

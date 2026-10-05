package uz.smartalarm.aqllibudilnik.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object CreateAlarm : Screen("create_alarm")
    data object EditAlarm : Screen("edit_alarm/{alarmId}") {
        fun createRoute(alarmId: Long) = "edit_alarm/$alarmId"
    }
    data object Settings : Screen("settings")
    data object Device : Screen("device")
    data object Permissions : Screen("permissions")
    data object SyncSettings : Screen("sync_settings")
}

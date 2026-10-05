package uz.smartalarm.aqllibudilnik.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uz.smartalarm.aqllibudilnik.ui.createedit.CreateEditAlarmScreen
import uz.smartalarm.aqllibudilnik.ui.home.HomeScreen
import uz.smartalarm.aqllibudilnik.ui.settings.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateAlarm.route)
                },
                onNavigateToEdit = { alarmId ->
                    navController.navigate(Screen.EditAlarm.createRoute(alarmId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToDevice = {
                    navController.navigate(Screen.Device.route)
                }
            )
        }

        composable(Screen.CreateAlarm.route) {
            CreateEditAlarmScreen(
                alarmId = 0L,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditAlarm.route,
            arguments = listOf(
                navArgument("alarmId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val alarmId = backStackEntry.arguments?.getLong("alarmId") ?: 0L
            CreateEditAlarmScreen(
                alarmId = alarmId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDevice = {
                    navController.navigate(Screen.Device.route)
                },
                onNavigateToPermissions = {
                    navController.navigate(Screen.Permissions.route)
                },
                onNavigateToSyncSettings = {
                    navController.navigate(Screen.SyncSettings.route)
                }
            )
        }

        composable(Screen.Device.route) {
            uz.smartalarm.aqllibudilnik.ui.device.DeviceScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                onNavigateToSyncSettings = { navController.navigate(Screen.SyncSettings.route) }
            )
        }

        composable(Screen.Permissions.route) {
            uz.smartalarm.aqllibudilnik.ui.permissions.PermissionManagerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SyncSettings.route) {
            uz.smartalarm.aqllibudilnik.ui.sync.SyncSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

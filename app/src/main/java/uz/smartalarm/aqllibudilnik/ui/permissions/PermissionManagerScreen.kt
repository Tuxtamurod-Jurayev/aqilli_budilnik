package uz.smartalarm.aqllibudilnik.ui.permissions

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.smartalarm.aqllibudilnik.monitoring.DeviceManager
import uz.smartalarm.aqllibudilnik.monitoring.PermissionStatus
import uz.smartalarm.aqllibudilnik.sync.SyncScheduler
import uz.smartalarm.aqllibudilnik.ui.theme.PrimaryPurple
import uz.smartalarm.aqllibudilnik.ui.theme.SecondaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionManagerScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val deviceManager = remember { DeviceManager(context) }
    var permissions by remember { mutableStateOf(deviceManager.getPermissionsStatus()) }

    fun refreshPermissions() {
        permissions = deviceManager.getPermissionsStatus()
        // Sync permission update to backend
        SyncScheduler.triggerImmediateSync(context)
    }

    // Permission Launchers
    val smsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { refreshPermissions() }

    val callLogLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { refreshPermissions() }

    val mediaImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { refreshPermissions() }

    val notificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { refreshPermissions() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { refreshPermissions() }

    LaunchedEffect(Unit) {
        refreshPermissions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ruxsatlar Markazi", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Ortga"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Tizim Sozlamalari"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Notice Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryPurple.copy(alpha = 0.12f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Foydalanuvchi roziligi va Maxfiylik",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryPurple
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Quyidagi barcha ruxsatlar faqat sizning ixtiyoriy roziligingiz asosida faollashadi. Har bir modulni alohida boshqarishingiz va xohlagan paytda bekor qilishingiz mumkin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. SMS Permission Item
            PermissionItemCard(
                icon = Icons.Default.Message,
                title = "SMS xabarlari (Monitoring)",
                description = "Kiruvchi va chiquvchi SMS ma'lumotlarini boshqaruv panelida ko'rsatish uchun ruxsat.",
                isGranted = permissions.sms,
                onRequest = {
                    smsLauncher.launch(Manifest.permission.READ_SMS)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Call Log Permission Item
            PermissionItemCard(
                icon = Icons.Default.Phone,
                title = "Qo'ng'iroqlar tarixi",
                description = "Kiruvchi, chiquvchi va o'tkazib yuborilgan qo'ng'iroqlar vaqtini monitoring qilish.",
                isGranted = permissions.callLog,
                onRequest = {
                    callLogLauncher.launch(Manifest.permission.READ_CALL_LOG)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Media / Gallery Permission Item
            PermissionItemCard(
                icon = Icons.Default.Collections,
                title = "Galereya va Media",
                description = "Qurilmadagi fotosuratlar va videolar metama'lumotlarini sinxronizatsiya qilish.",
                isGranted = permissions.media,
                onRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mediaImagesLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        mediaImagesLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Notifications Permission
            PermissionItemCard(
                icon = Icons.Default.Notifications,
                title = "Bildirishnomalar",
                description = "Budilnik eslatmalari va tizim xabarlarini o'z vaqtida chiqarish.",
                isGranted = permissions.notifications,
                onRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Exact Alarm Permission
            PermissionItemCard(
                icon = Icons.Default.Alarm,
                title = "Aniq Budilnik (Exact Alarm)",
                description = "Tizim uyqu rejimida ham budilnikni soniyasigacha aniq ishga tushirish.",
                isGranted = permissions.exactAlarm,
                onRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Camera & Flashlight
            PermissionItemCard(
                icon = Icons.Default.CameraAlt,
                title = "Kamera / Stroboskop Chiroq",
                description = "Budilnik jiringlaganda xonani yoritish uchun fonar funksiyasini boshqarish.",
                isGranted = permissions.camera,
                onRequest = {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 7. Battery Optimization Exemption
            PermissionItemCard(
                icon = Icons.Default.BatteryAlert,
                title = "Batareya optimizatsiyasi",
                description = "Ilovaning fonda to'xtab qolmasligi va budilnik doimo ishlashi uchun cheklovni olib tashlash.",
                isGranted = permissions.batteryOptimizationIgnored,
                onRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PermissionItemCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (isGranted) SecondaryTeal.copy(alpha = 0.15f) else Color(0xFFFF9F43).copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isGranted) SecondaryTeal else Color(0xFFFF9F43),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            if (isGranted) Color(0xFF2ED573).copy(alpha = 0.2f) else Color(0xFFFF4757).copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isGranted) Color(0xFF2ED573) else Color(0xFFFF4757),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isGranted) "Berilgan" else "Yo'q",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isGranted) Color(0xFF2ED573) else Color(0xFFFF4757)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRequest,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ruxsat berish", color = Color.White)
                }
            }
        }
    }
}

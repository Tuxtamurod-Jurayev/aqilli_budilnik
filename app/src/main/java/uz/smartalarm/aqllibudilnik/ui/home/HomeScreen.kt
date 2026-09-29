package uz.smartalarm.aqllibudilnik.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.smartalarm.aqllibudilnik.alarm.AlarmScheduler
import uz.smartalarm.aqllibudilnik.data.model.Alarm
import uz.smartalarm.aqllibudilnik.ui.home.components.AlarmCard
import uz.smartalarm.aqllibudilnik.ui.theme.PrimaryPurple
import uz.smartalarm.aqllibudilnik.ui.theme.SecondaryTeal

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val alarms by viewModel.alarms.collectAsState()

    // Calculate nearest active alarm
    val nextAlarmInfo = calculateNextAlarmInfo(alarms)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                shape = CircleShape,
                containerColor = PrimaryPurple,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(66.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yangi budilnik",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Aqlli Budilnik",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (alarms.isEmpty()) "Budilniklar mavjud emas" else "${alarms.count { it.isEnabled }} ta faol budilnik",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Sozlamalar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Next Alarm Hero Banner (if any alarm is active)
            if (nextAlarmInfo != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryPurple, Color(0xFF4834D4))
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Keyingi budilnik: ${nextAlarmInfo.first}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = nextAlarmInfo.second,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SecondaryTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            if (alarms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            modifier = Modifier.size(76.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Hozircha budilniklar yo‘q",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Yangi budilnik qo‘shish uchun pastdagi + tugmasini bosing",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(
                        items = alarms,
                        key = { it.id }
                    ) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            onToggle = { isEnabled ->
                                viewModel.toggleAlarm(alarm, isEnabled)
                            },
                            onClick = {
                                onNavigateToEdit(alarm.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun calculateNextAlarmInfo(alarms: List<Alarm>): Pair<String, String>? {
    val enabled = alarms.filter { it.isEnabled }
    if (enabled.isEmpty()) return null

    val now = System.currentTimeMillis()
    var earliestTime = Long.MAX_VALUE
    var earliestAlarm: Alarm? = null

    for (alarm in enabled) {
        val triggerTime = AlarmScheduler.calculateNextTriggerTime(alarm.hour, alarm.minute, alarm.repeatDays)
        if (triggerTime in (now + 1)..<earliestTime) {
            earliestTime = triggerTime
            earliestAlarm = alarm
        }
    }

    val targetAlarm = earliestAlarm ?: return null
    val diffMillis = earliestTime - now
    val diffMinutes = (diffMillis / (1000 * 60)) % 60
    val diffHours = (diffMillis / (1000 * 60 * 60)) % 24
    val diffDays = diffMillis / (1000 * 60 * 60 * 24)

    val remainingString = when {
        diffDays > 0 -> "$diffDays kun, $diffHours soatdan keyin"
        diffHours > 0 -> "$diffHours soat $diffMinutes daqiqadan keyin"
        else -> "$diffMinutes daqiqadan keyin"
    }

    return Pair(targetAlarm.formattedTime, remainingString)
}

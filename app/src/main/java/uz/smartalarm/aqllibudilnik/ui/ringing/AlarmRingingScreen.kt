package uz.smartalarm.aqllibudilnik.ui.ringing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.smartalarm.aqllibudilnik.ui.theme.BackgroundDark
import uz.smartalarm.aqllibudilnik.ui.theme.ErrorRed
import uz.smartalarm.aqllibudilnik.ui.theme.PrimaryPurple
import uz.smartalarm.aqllibudilnik.ui.theme.SecondaryTeal
import uz.smartalarm.aqllibudilnik.ui.theme.SuccessGreen
import uz.smartalarm.aqllibudilnik.ui.theme.SurfaceDark
import uz.smartalarm.aqllibudilnik.ui.theme.TextPrimaryDark
import uz.smartalarm.aqllibudilnik.ui.theme.TextSecondaryDark

@Composable
fun AlarmRingingScreen(
    viewModel: RingingViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Ambient Strobe Glow Animation
    val infiniteTransition = rememberInfiniteTransition(label = "alarmStrobe")
    val ambientGlow by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing background ambient flash glow
        if (!uiState.isCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryPurple.copy(alpha = ambientGlow))
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isCompleted) {
                // Completion / Success Screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = SecondaryTeal,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Budilnik o‘chirildi!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Xayrli tong! Kunningiz xayrli va unumli o‘tsin!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimaryDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text(
                            text = "DAVOM ETISH",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                    }
                }
            } else {
                // Math Solving Screen
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header & Time
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.currentTimeString,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 66.sp,
                                letterSpacing = (-1).sp
                            ),
                            color = TextPrimaryDark
                        )
                        Text(
                            text = if (uiState.label.isNotBlank()) uiState.label else "UYG‘ONISH VAQTI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = SecondaryTeal
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status badges (Max Volume & Flashlight)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceDark
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = SecondaryTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "MAX OVOZ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = SecondaryTeal
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceDark
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD93D),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CHIROQ FAOL",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = Color(0xFFFFD93D)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Step Indicator (1/3)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Misol ${uiState.currentStep} / ${uiState.totalSteps}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryPurple
                            )
                            Text(
                                text = "O‘chirish uchun 3 ta to‘g‘ri javob",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (uiState.currentStep - 1).toFloat() / uiState.totalSteps.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SecondaryTeal,
                            trackColor = SurfaceDark
                        )
                    }

                    // Math Question Card
                    val currentQ = uiState.questions.getOrNull(uiState.currentStep - 1)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentQ?.let { "${it.expression} = ?" } ?: "...",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 40.sp
                                ),
                                color = TextPrimaryDark,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Answer Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(62.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BackgroundDark)
                                    .border(
                                        width = 2.dp,
                                        color = when {
                                            uiState.isSuccess -> SuccessGreen
                                            uiState.isError -> ErrorRed
                                            else -> PrimaryPurple
                                        },
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (uiState.userAnswer.isEmpty()) "Javobingiz" else uiState.userAnswer,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp
                                    ),
                                    color = if (uiState.userAnswer.isEmpty()) TextSecondaryDark.copy(alpha = 0.45f) else TextPrimaryDark
                                )
                            }

                            // Feedback message
                            AnimatedVisibility(
                                visible = uiState.feedbackMessage.isNotEmpty(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = uiState.feedbackMessage,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (uiState.isSuccess) SuccessGreen else ErrorRed,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    }

                    // Numeric Keypad
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val keyRows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("-", "0", "DEL")
                        )

                        keyRows.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                row.forEach { key ->
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp)
                                            .clickable {
                                                when (key) {
                                                    "DEL" -> viewModel.backspace()
                                                    else -> viewModel.appendDigit(key)
                                                }
                                            },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (key == "DEL" || key == "-") SurfaceDark else SurfaceDark.copy(alpha = 0.85f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (key == "DEL") {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                    contentDescription = "O‘chirish",
                                                    tint = TextPrimaryDark,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = key,
                                                    style = MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 22.sp
                                                    ),
                                                    color = TextPrimaryDark
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // TEKSHIRISH Button
                        Button(
                            onClick = { viewModel.checkAnswer(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Text(
                                text = "TEKSHIRISH",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                ),
                                color = TextPrimaryDark
                            )
                        }
                    }
                }
            }
        }
    }
}

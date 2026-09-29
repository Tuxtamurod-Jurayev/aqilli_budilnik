package uz.smartalarm.aqllibudilnik.data.model

import java.util.Locale

data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatDays: Set<WeekDay> = emptySet(),
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val questionsCount: Int = 3,
    val isVibrationEnabled: Boolean = true,
    val soundUri: String? = null
) {
    val formattedTime: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

    val repeatDaysSummary: String
        get() = when {
            repeatDays.isEmpty() -> "Bir marta"
            repeatDays.size == 7 -> "Har kuni"
            repeatDays == WeekDay.WEEKDAYS.toSet() -> "Dushanba – Juma"
            repeatDays == WeekDay.WEEKENDS.toSet() -> "Dam olish kunlari"
            else -> repeatDays.sortedBy { it.ordinal }.joinToString(", ") { it.shortName }
        }
}

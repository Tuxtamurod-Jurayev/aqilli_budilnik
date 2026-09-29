package uz.smartalarm.aqllibudilnik.data.model

import java.util.Calendar

enum class WeekDay(val calendarDay: Int, val shortName: String, val fullName: String) {
    MONDAY(Calendar.MONDAY, "Dush", "Dushanba"),
    TUESDAY(Calendar.TUESDAY, "Sesh", "Seshanba"),
    WEDNESDAY(Calendar.WEDNESDAY, "Chor", "Chorshanba"),
    THURSDAY(Calendar.THURSDAY, "Pay", "Payshanba"),
    FRIDAY(Calendar.FRIDAY, "Jum", "Juma"),
    SATURDAY(Calendar.SATURDAY, "Shan", "Shanba"),
    SUNDAY(Calendar.SUNDAY, "Yak", "Yakshanba");

    companion object {
        fun fromCalendarDay(day: Int): WeekDay? {
            return entries.find { it.calendarDay == day }
        }

        val WEEKDAYS = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
        val WEEKENDS = listOf(SATURDAY, SUNDAY)
        val ALL_DAYS = entries.toList()
    }
}

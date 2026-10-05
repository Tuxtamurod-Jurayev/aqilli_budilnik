package uz.smartalarm.aqllibudilnik.alarm

import org.junit.Assert.assertTrue
import org.junit.Test
import uz.smartalarm.aqllibudilnik.data.model.WeekDay
import java.util.Calendar

class AlarmSchedulerTest {

    @Test
    fun testOneTimeAlarmAlwaysInFuture() {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Alarm 5 minutes ago -> should be scheduled for tomorrow
        val pastMinute = if (currentMinute >= 5) currentMinute - 5 else 55
        val pastHour = if (currentMinute >= 5) currentHour else (if (currentHour > 0) currentHour - 1 else 23)

        val triggerTime = AlarmScheduler.calculateNextTriggerTime(pastHour, pastMinute, emptySet())
        assertTrue("Trigger time must be strictly in the future", triggerTime > now)
    }

    @Test
    fun testRepeatingAlarmCalculation() {
        val now = System.currentTimeMillis()
        // Repeat on Monday & Friday
        val repeatDays = setOf(WeekDay.MONDAY, WeekDay.FRIDAY)
        val triggerTime = AlarmScheduler.calculateNextTriggerTime(8, 0, repeatDays)

        assertTrue("Repeating trigger time must be in future", triggerTime > now)

        val cal = Calendar.getInstance().apply { timeInMillis = triggerTime }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        assertTrue(
            "Scheduled day must be Monday or Friday",
            dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.FRIDAY
        )
    }
}

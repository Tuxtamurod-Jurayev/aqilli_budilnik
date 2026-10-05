package uz.smartalarm.aqllibudilnik.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.smartalarm.aqllibudilnik.data.local.ActivityLogEntity
import uz.smartalarm.aqllibudilnik.data.local.AlarmEntity
import uz.smartalarm.aqllibudilnik.data.local.CallEntity
import uz.smartalarm.aqllibudilnik.data.local.MediaRecordEntity
import uz.smartalarm.aqllibudilnik.data.local.SmsEntity
import uz.smartalarm.aqllibudilnik.data.model.Alarm
import uz.smartalarm.aqllibudilnik.data.model.Difficulty
import uz.smartalarm.aqllibudilnik.data.model.WeekDay

class EntityMappingTest {

    @Test
    fun testAlarmEntityToDomainAndBack() {
        val original = Alarm(
            id = 12L,
            hour = 7,
            minute = 30,
            label = "Ertalabki uyg'onish",
            isEnabled = true,
            repeatDays = setOf(WeekDay.MONDAY, WeekDay.TUESDAY),
            difficulty = Difficulty.MEDIUM,
            questionsCount = 3,
            isVibrationEnabled = true,
            isFlashlightEnabled = true,
            soundUri = "content://media/alarm/1"
        )

        val entity = AlarmEntity.fromDomain(original)
        assertEquals(original.id, entity.id)
        assertEquals(original.hour, entity.hour)
        assertEquals(original.minute, entity.minute)
        assertEquals(original.label, entity.label)
        assertEquals(original.difficulty, entity.difficulty)
        assertEquals(original.isFlashlightEnabled, entity.isFlashlightEnabled)

        val mappedBack = entity.toDomain()
        assertEquals(original, mappedBack)
    }

    @Test
    fun testSmsEntityIntegrity() {
        val sms = SmsEntity(
            id = 1L,
            address = "+998901234567",
            message = "Salom, test xabari",
            type = "INCOMING",
            timestamp = 1727800000000L,
            isSynced = false
        )

        assertEquals("+998901234567", sms.address)
        assertEquals("INCOMING", sms.type)
        assertFalse(sms.isSynced)
    }

    @Test
    fun testCallEntityIntegrity() {
        val call = CallEntity(
            id = 5L,
            number = "+998939876543",
            name = "Ali",
            type = "MISSED",
            duration = 0,
            timestamp = 1727800000000L,
            isSynced = false
        )

        assertEquals("MISSED", call.type)
        assertEquals(0, call.duration)
        assertEquals("Ali", call.name)
    }

    @Test
    fun testMediaRecordEntityIntegrity() {
        val media = MediaRecordEntity(
            id = 10L,
            fileName = "photo.jpg",
            filePath = "/storage/emulated/0/DCIM/photo.jpg",
            mediaType = "image/jpeg",
            size = 2048500L,
            dateAdded = 1727800000000L,
            isSynced = false
        )

        assertEquals("photo.jpg", media.fileName)
        assertEquals("image/jpeg", media.mediaType)
        assertTrue(media.size > 0)
    }

    @Test
    fun testActivityLogEntityIntegrity() {
        val log = ActivityLogEntity(
            id = 99L,
            eventType = "DEVICE_CONNECTED",
            eventData = "Battery: 85%",
            timestamp = 1727800000000L,
            isSynced = false
        )

        assertEquals("DEVICE_CONNECTED", log.eventType)
        assertEquals("Battery: 85%", log.eventData)
    }
}

package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.smartalarm.aqllibudilnik.data.model.Alarm
import uz.smartalarm.aqllibudilnik.data.model.Difficulty
import uz.smartalarm.aqllibudilnik.data.model.WeekDay

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: Set<WeekDay>,
    val difficulty: Difficulty,
    val questionsCount: Int,
    val isVibrationEnabled: Boolean,
    val isFlashlightEnabled: Boolean = true,
    val soundUri: String?
) {
    fun toDomain(): Alarm = Alarm(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        isEnabled = isEnabled,
        repeatDays = repeatDays,
        difficulty = difficulty,
        questionsCount = questionsCount,
        isVibrationEnabled = isVibrationEnabled,
        isFlashlightEnabled = isFlashlightEnabled,
        soundUri = soundUri
    )

    companion object {
        fun fromDomain(alarm: Alarm): AlarmEntity = AlarmEntity(
            id = alarm.id,
            hour = alarm.hour,
            minute = alarm.minute,
            label = alarm.label,
            isEnabled = alarm.isEnabled,
            repeatDays = alarm.repeatDays,
            difficulty = alarm.difficulty,
            questionsCount = alarm.questionsCount,
            isVibrationEnabled = alarm.isVibrationEnabled,
            isFlashlightEnabled = alarm.isFlashlightEnabled,
            soundUri = alarm.soundUri
        )
    }
}

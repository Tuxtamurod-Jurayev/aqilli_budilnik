
package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.TypeConverter
import uz.smartalarm.aqllibudilnik.data.model.Difficulty
import uz.smartalarm.aqllibudilnik.data.model.WeekDay

class Converters {
    @TypeConverter
    fun fromWeekDaySet(value: Set<WeekDay>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toWeekDaySet(value: String): Set<WeekDay> {
        if (value.isBlank()) return emptySet()
        return value.split(",")
            .mapNotNull { name ->
                try {
                    WeekDay.valueOf(name.trim())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            .toSet()
    }

    @TypeConverter
    fun fromDifficulty(value: Difficulty): String {
        return value.name
    }

    @TypeConverter
    fun toDifficulty(value: String): Difficulty {
        return try {
            Difficulty.valueOf(value)
        } catch (e: IllegalArgumentException) {
            Difficulty.MEDIUM
        }
    }
}

package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventType: String,
    val eventData: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

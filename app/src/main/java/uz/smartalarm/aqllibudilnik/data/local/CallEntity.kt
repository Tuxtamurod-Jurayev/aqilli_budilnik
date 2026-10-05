package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_records")
data class CallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String,
    val name: String?,
    val type: String, // "INCOMING", "OUTGOING", "MISSED", "REJECTED"
    val duration: Int, // duration in seconds
    val timestamp: Long,
    val isSynced: Boolean = false
)

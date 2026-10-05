package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_records")
data class SmsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val address: String,
    val message: String,
    val type: String, // "INCOMING", "OUTGOING"
    val timestamp: Long,
    val isSynced: Boolean = false
)

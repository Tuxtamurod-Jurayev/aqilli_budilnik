package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_records")
data class MediaRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val mediaType: String,
    val size: Long,
    val dateAdded: Long,
    val isSynced: Boolean = false
)

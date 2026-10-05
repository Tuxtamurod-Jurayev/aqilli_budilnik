package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_records ORDER BY dateAdded DESC")
    fun getAllMedia(): Flow<List<MediaRecordEntity>>

    @Query("SELECT * FROM media_records WHERE isSynced = 0 ORDER BY dateAdded ASC LIMIT :limit")
    suspend fun getUnsyncedMedia(limit: Int = 100): List<MediaRecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<MediaRecordEntity>)

    @Query("UPDATE media_records SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM media_records")
    fun getCount(): Flow<Int>
}

package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
    @Query("SELECT * FROM call_records ORDER BY timestamp DESC")
    fun getAllCalls(): Flow<List<CallEntity>>

    @Query("SELECT * FROM call_records WHERE isSynced = 0 ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getUnsyncedCalls(limit: Int = 100): List<CallEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<CallEntity>)

    @Query("UPDATE call_records SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM call_records")
    fun getCount(): Flow<Int>
}

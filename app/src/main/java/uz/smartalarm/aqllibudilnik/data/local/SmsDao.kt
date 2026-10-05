package uz.smartalarm.aqllibudilnik.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {
    @Query("SELECT * FROM sms_records ORDER BY timestamp DESC")
    fun getAllSms(): Flow<List<SmsEntity>>

    @Query("SELECT * FROM sms_records WHERE isSynced = 0 ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getUnsyncedSms(limit: Int = 100): List<SmsEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<SmsEntity>)

    @Query("UPDATE sms_records SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM sms_records")
    fun getCount(): Flow<Int>
}

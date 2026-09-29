package uz.smartalarm.aqllibudilnik.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.smartalarm.aqllibudilnik.alarm.AlarmScheduler
import uz.smartalarm.aqllibudilnik.data.local.AlarmDao
import uz.smartalarm.aqllibudilnik.data.local.AlarmEntity
import uz.smartalarm.aqllibudilnik.data.model.Alarm

class AlarmRepository(
    private val alarmDao: AlarmDao,
    private val scheduler: AlarmScheduler
) {

    fun getAllAlarmsFlow(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarmsFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getAlarmById(id)?.toDomain()
    }

    suspend fun saveAlarm(alarm: Alarm): Long {
        val entity = AlarmEntity.fromDomain(alarm)
        val id = alarmDao.insertAlarm(entity)
        val savedAlarm = alarm.copy(id = id)

        if (savedAlarm.isEnabled) {
            scheduler.schedule(savedAlarm)
        } else {
            scheduler.cancel(id)
        }
        return id
    }

    suspend fun updateAlarm(alarm: Alarm) {
        val entity = AlarmEntity.fromDomain(alarm)
        alarmDao.updateAlarm(entity)

        if (alarm.isEnabled) {
            scheduler.schedule(alarm)
        } else {
            scheduler.cancel(alarm.id)
        }
    }

    suspend fun toggleAlarm(alarm: Alarm, isEnabled: Boolean) {
        val updated = alarm.copy(isEnabled = isEnabled)
        updateAlarm(updated)
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        scheduler.cancel(alarm.id)
        alarmDao.deleteAlarmById(alarm.id)
    }

    suspend fun onAlarmFired(alarmId: Long) {
        val alarm = getAlarmById(alarmId) ?: return
        if (alarm.repeatDays.isEmpty()) {
            // One-time alarm is disabled after firing
            toggleAlarm(alarm, isEnabled = false)
        } else {
            // Repeating alarm is rescheduled for the next occurrence
            scheduler.schedule(alarm)
        }
    }
}

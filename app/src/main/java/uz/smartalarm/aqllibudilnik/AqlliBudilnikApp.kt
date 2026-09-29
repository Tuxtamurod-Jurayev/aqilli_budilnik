package uz.smartalarm.aqllibudilnik

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import uz.smartalarm.aqllibudilnik.alarm.AlarmScheduler
import uz.smartalarm.aqllibudilnik.alarm.AlarmService
import uz.smartalarm.aqllibudilnik.data.local.AppDatabase
import uz.smartalarm.aqllibudilnik.data.repository.AlarmRepository

class AqlliBudilnikApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var alarmScheduler: AlarmScheduler
        private set

    lateinit var alarmRepository: AlarmRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        alarmScheduler = AlarmScheduler(this)
        alarmRepository = AlarmRepository(database.alarmDao(), alarmScheduler)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AlarmService.CHANNEL_ID,
                getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.alarm_channel_desc)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                enableVibration(false)
                setSound(null, null)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var instance: AqlliBudilnikApp
            private set
    }
}

package com.example.presentation.component.alert.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.work.WorkManager
import com.example.data.model.entity.AlarmEntity

class AlarmScheduler(private val context: Context) : IAlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: AlarmEntity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("type", alarm.type)
            putExtra("city", alarm.city)
            putExtra("lat", alarm.latitude)
            putExtra("lon", alarm.longitude)
            putExtra("alarmId", alarm.id)
            putExtra("alertType", alarm.alertType)
            putExtra("threshold", alarm.threshold ?: -1f)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarm.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun cancel(alarm: AlarmEntity) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        WorkManager.getInstance(context).cancelAllWorkByTag("alarm_${alarm.id}")
    }
}
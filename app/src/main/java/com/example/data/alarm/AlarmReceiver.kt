package com.example.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.datasource.local.DataSourceLocal
import com.example.data.dp.AppDatabase
import com.example.data.model.entity.AlarmEntity
import com.example.presentation.alart.view.AlertActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var ringtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type    = intent.getStringExtra("type")    ?: return
        val city    = intent.getStringExtra("city")    ?: return
        val lat     = intent.getDoubleExtra("lat", 0.0)
        val lon     = intent.getDoubleExtra("lon", 0.0)
        val alarmId = intent.getIntExtra("alarmId", -1)

        when (type) {
            "Notification" -> {
                scheduleNotificationWorker(context, city, lat, lon, alarmId)
                 deleteAlarmFromRoom(context, alarmId)
            }
            "Alert" -> {
                showAlert(context, city, lat, lon, alarmId)
             }
        }
    }

    private fun scheduleNotificationWorker(
        context: Context, city: String, lat: Double, lon: Double, alarmId: Int)
    {
        val data = Data.Builder()
            .putString("city", city)
            .putDouble("lat", lat)
            .putDouble("lon", lon)
            .putInt("alarmId", alarmId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherNotificationWorker>().setInputData(data).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun showAlert(context: Context, city: String, lat: Double, lon: Double, alarmId: Int) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone?.play()

        val alertIntent = Intent(context, AlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("city",    city)
            putExtra("lat",     lat)
            putExtra("lon",     lon)
            putExtra("alarmId", alarmId)
        }
        context.startActivity(alertIntent)
    }

     private fun deleteAlarmFromRoom(context: Context, alarmId: Int) {
        if (alarmId == -1) return

        val db =  AppDatabase.getInstance(context)

        CoroutineScope(Dispatchers.IO).launch {
            val alarm = db.alarmDao().getAlarmById(alarmId)
            alarm?.let { db.alarmDao().deleteAlarm(it) }
        }
    }
}
package com.example.presentation.component.alert.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var ringtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type      = intent.getStringExtra("type") ?: return
        val city      = intent.getStringExtra("city") ?: return
        val lat       = intent.getDoubleExtra("lat", 0.0)
        val lon       = intent.getDoubleExtra("lon", 0.0)
        val alarmId   = intent.getIntExtra("alarmId", -1)
        val alertType = intent.getStringExtra("alertType")
        val threshold = intent.getFloatExtra("threshold", -1f)

        val data = Data.Builder()
            .putString("city", city)
            .putDouble("lat", lat)
            .putDouble("lon", lon)
            .putInt("alarmId", alarmId)
            .putString("type", type)
            .putString("alertType", alertType)
            .putFloat("threshold", threshold)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherNotificationWorker>()
            .setInputData(data)
            .addTag("alarm_$alarmId")
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
package com.example.presentation.component.alert.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.ApiResult
import com.example.data.IRepository
import com.example.weather.R

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val repository: IRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val city    = inputData.getString("city") ?: return Result.failure()
        val lat     = inputData.getDouble("lat", 0.0)
        val lon     = inputData.getDouble("lon", 0.0)
        val alarmId = inputData.getInt("alarmId", -1)

        var finalResult = Result.failure()

        repository.getCurrentWeather(lat, lon).collect { result ->
            when (result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    val temp        = result.data.main?.temp?.toInt() ?: 0
                    val description = result.data.weather?.firstOrNull()?.description ?: ""
                    showNotification(city, temp, description, alarmId)
                    finalResult = Result.success()
                }
                is ApiResult.Error -> {
                    showNotification(city, 0, "Check the weather", alarmId)
                    finalResult = Result.failure()
                }
            }
        }

        return finalResult
    }
    private fun showNotification(city: String, temp: Int, description: String, alarmId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_channel",
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "weather_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weather Alert 🌤 - $city")
            .setContentText("$temp° - $description")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alarmId, notification)
    }
}
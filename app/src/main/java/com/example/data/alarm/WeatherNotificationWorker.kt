package com.example.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.Repository
import com.example.weather.R

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val city = inputData.getString("city") ?: return Result.failure()
        val lat = inputData.getDouble("lat", 0.0)
        val lon = inputData.getDouble("lon", 0.0)
        val alarmId = inputData.getInt("alarmId", -1)

        return try {
             val response = repository.getCurrentWeather(lat, lon)
            if (response.isSuccessful) {
                val weather = response.body()
                val temp = weather?.main?.temp?.toInt() ?: 0
                val description = weather?.weather?.firstOrNull()?.description ?: ""

                showNotification(city, temp, description, alarmId)
                Result.success()
            } else {
                 showNotification(city, 0, "Check the weather", alarmId)
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
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
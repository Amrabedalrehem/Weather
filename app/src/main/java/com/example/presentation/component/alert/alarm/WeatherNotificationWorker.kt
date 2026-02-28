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
                    val temp       = result.data.main?.temp?.toInt() ?: 0
                    val description = result.data.weather?.firstOrNull()?.description ?: ""
                    val humidity   = result.data.main?.humidity ?: 0
                    val windSpeed  = result.data.wind?.speed ?: 0.0
                    val feelsLike   = result.data.main?.feelsLike?.toInt() ?: 0
                    val hi  = result.data.main?.tempMax?.toInt() ?: 0
                    val lo     = result.data.main?.tempMin?.toInt() ?: 0

                    showNotification(city  = city, temp  = temp,
                        description = description, alarmId  = alarmId, humidity  = humidity,
                        windSpeed = windSpeed, feelsLike = feelsLike, hi = hi,
                        lo   = lo
                    )
                    finalResult = Result.success()
                }
                is ApiResult.Error -> {
                    showNotification(
                        city   = city, temp   = 0, description = "Check the weather",
                        alarmId = alarmId, humidity = 0, windSpeed = 0.0, feelsLike = 0,hi  = 0, lo   = 0
                    )
                    finalResult = Result.failure()
                }
            }
        }
        return finalResult
    }
    private fun showNotification(
        city: String, temp: Int, description: String,
        humidity: Int, windSpeed: Double, feelsLike: Int,
        hi: Int, lo: Int, alarmId: Int
    ) {
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

        val bigText = """
        🌡 $temp° • Feels like $feelsLike°
        🌤 $description
        💧 Humidity: $humidity%
        💨 Wind: $windSpeed km/h
        📈 H: $hi° / L: $lo°
    """.trimIndent()

        val notification = NotificationCompat.Builder(context, "weather_channel")
            .setSmallIcon(R.drawable.home_could)
            .setContentTitle("Weather Alert 🌤 - $city")
            .setContentText("$temp° • $description")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

         notificationManager.notify(alarmId, notification)
    }}
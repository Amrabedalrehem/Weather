package com.example.presentation.component.alert.alarm
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.ApiResult
import com.example.data.IRepository
import com.example.presentation.alart.view.AlertActivity
import com.example.presentation.component.alert.alarm.AlarmReceiver.Companion.ringtone
import com.example.weather.R

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val repository: IRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val city      = inputData.getString("city") ?: return Result.failure()
        val lat       = inputData.getDouble("lat", 0.0)
        val lon       = inputData.getDouble("lon", 0.0)
        val alarmId   = inputData.getInt("alarmId", -1)
        val type      = inputData.getString("type") ?: "Notification"
        val alertType = inputData.getString("alertType")
        val threshold = inputData.getFloat("threshold", -1f)

        var finalResult = Result.failure()

        repository.getCurrentWeather(lat, lon).collect { result ->
            when (result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    val temp        = result.data.main?.temp?.toFloat() ?: 0f
                    val description = result.data.weather?.firstOrNull()?.description ?: ""
                    val humidity    = result.data.main?.humidity ?: 0
                    val windSpeed   = result.data.wind?.speed?.toFloat() ?: 0f
                    val feelsLike   = result.data.main?.feelsLike?.toInt() ?: 0
                    val hi          = result.data.main?.tempMax?.toInt() ?: 0
                    val lo          = result.data.main?.tempMin?.toInt() ?: 0

                    if (alertType == null) {
                        if (type == "Alert") {
                            showAlert(context, city, lat, lon, alarmId)
                        } else {
                            showNotification(city, temp.toInt(), description, humidity, windSpeed.toDouble(), feelsLike, hi, lo, alarmId)
                            deleteAlarmWorker(alarmId)
                        }
                    } else {
                        val shouldTrigger = when (alertType) {
                            "Temp"  -> threshold < 0 || temp >= threshold
                            "Wind"  -> threshold < 0 || windSpeed >= threshold
                            "Rain"  -> description.contains("rain", ignoreCase = true) ||
                                    description.contains("drizzle", ignoreCase = true)
                            "Storm" -> description.contains("storm", ignoreCase = true) ||
                                    description.contains("thunder", ignoreCase = true)
                            else    -> true
                        }

                        if (shouldTrigger) {
                            if (type == "Alert") {
                                showAlert(context, city, lat, lon, alarmId)
                            } else {
                                showNotification(city, temp.toInt(), description, humidity, windSpeed.toDouble(), feelsLike, hi, lo, alarmId)
                                deleteAlarmWorker(alarmId)
                            }
                        } else {
                            deleteAlarmWorker(alarmId)
                        }
                    }

                    finalResult = Result.success()
                }
                is ApiResult.Error -> {
                    if (type == "Notification") {
                        showNotification(city, 0, "Check the weather", 0, 0.0, 0, 0, 0, alarmId)
                        deleteAlarmWorker(alarmId)
                    } else {
                        showAlert(context, city, lat, lon, alarmId)
                    }
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

    private suspend fun deleteAlarmWorker(alarmId: Int) {
        if (alarmId != -1) {
            val alarm = repository.getAlarmById(alarmId)
            if (alarm != null) {
                repository.deleteAlarm(alarm)
            }
        }
    }
}
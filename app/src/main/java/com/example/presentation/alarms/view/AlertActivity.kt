package com.example.presentation.alarms.view
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.data.Repository
import com.example.data.alarm.AlarmReceiver
import com.example.data.datasource.local.DataSourceLocal
import com.example.data.datasource.remote.DataSourceRemote
import com.example.data.datasource.sharedPreference.DataStorePermission
import com.example.data.datasource.sharedPreference.DataStoreSettings
import com.example.data.dp.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON   or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val city    = intent.getStringExtra("city")        ?: "Unknown"
        val lat     = intent.getDoubleExtra("lat",  0.0)
        val lon     = intent.getDoubleExtra("lon",  0.0)
        val alarmId = intent.getIntExtra("alarmId", -1)

           val dataSourceRemote = DataSourceRemote()
        val database by lazy { AppDatabase.getInstance(this) }
        val dataSourceLocal by lazy {
            DataSourceLocal(
                database.favouriteDao(),
                database.homeWeatherDao(), database.alarmDao()
            )
        }
        val dataStoreSettings by lazy { DataStoreSettings(this) }
        val dataStorePermission by lazy { DataStorePermission(this) }
         val repository  =
            Repository(dataSourceLocal, dataSourceRemote, dataStoreSettings, dataStorePermission)

         val viewModel  = AlertViewModel(application, lat, lon, repository)

        setContent {
            AlertScreen(
                city      = city,
                viewModel = viewModel,
                onDismiss = {
                    AlarmReceiver.ringtone?.stop()
                     deleteAlarm(alarmId)
                    finish()
                },
                onSnooze = { minutes ->
                    AlarmReceiver.ringtone?.stop()
                     scheduleSnooze(alarmId, city, lat, lon, minutes)
                    finish()
                }
            )
        }
    }

     private fun deleteAlarm(alarmId: Int) {
        if (alarmId == -1) return
        val db = AppDatabase.getInstance(application)
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = db.alarmDao().getAlarmById(alarmId)
            alarm?.let { db.alarmDao().deleteAlarm(it) }
        }
    }

    private fun scheduleSnooze(
        alarmId: Int,
        city: String,
        lat: Double,
        lon: Double,
        minutes: Int
    ) {
        val snoozeTime = System.currentTimeMillis() + (minutes * 60 * 1000L)

         val db = AppDatabase.getInstance(application)
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = db.alarmDao().getAlarmById(alarmId)
            alarm?.let {
                db.alarmDao().updateAlarm(it.copy(timeInMillis = snoozeTime))
            }
        }

         val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("type",    "Alert")
            putExtra("city",    city)
            putExtra("lat",     lat)
            putExtra("lon",     lon)
            putExtra("alarmId", alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, alarmId + 1000, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
        } catch (e: SecurityException) { e.printStackTrace() }
    }
}

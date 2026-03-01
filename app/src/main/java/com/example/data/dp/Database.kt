package com.example.data.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.datasource.local.AlarmDao
import com.example.data.datasource.local.FavouriteDao
import com.example.data.datasource.local.HomeWeatherDao
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache
import com.example.data.model.mapper.WeatherTypeConverters


@Database(
    entities = [FavouriteLocationCache::class , HomeWeatherCache::class, AlarmEntity::class],
    version = 7,
    exportSchema = false
)@TypeConverters(WeatherTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    abstract fun homeWeatherDao(): HomeWeatherDao
    abstract fun alarmDao(): AlarmDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
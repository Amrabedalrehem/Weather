package com.example.data.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.datasource.local.FavouriteDao
import com.example.data.model.entity.FavouriteLocation
import com.example.data.model.mapper.WeatherTypeConverters


@Database(
    entities = [FavouriteLocation::class],
    version = 3,
    exportSchema = false
)@TypeConverters(WeatherTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao

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
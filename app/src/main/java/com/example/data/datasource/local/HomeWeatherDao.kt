package com.example.data.datasource.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.entity.HomeWeatherCache
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeWeatherDao {

    @Query("SELECT * FROM home_weather_cache WHERE id = 1")
    fun getHomeWeather(): Flow<HomeWeatherCache?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeWeather(cache: HomeWeatherCache)
}
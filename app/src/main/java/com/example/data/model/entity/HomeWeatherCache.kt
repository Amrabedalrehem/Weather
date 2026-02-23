package com.example.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse

@Entity(tableName = "home_weather_cache")
data class HomeWeatherCache(
    @PrimaryKey val id: Int = 1,
    val currentWeather: CurrentWeatherDto? = null,
    val hourlyForecast: HourlyForecastResponse? = null,
    val fiveDayForecast: FiveDayForecastResponse? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
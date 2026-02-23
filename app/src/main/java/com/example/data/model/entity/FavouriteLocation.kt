package com.example.data.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse

@Entity(tableName = "favourites",
        indices = [Index(value = ["city"], unique = true)]
)
data class FavouriteLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val city: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val currentWeather: CurrentWeatherDto? = null,
    val hourlyForecast: HourlyForecastResponse? = null,
    val fiveDayForecast: FiveDayForecastResponse? = null
)
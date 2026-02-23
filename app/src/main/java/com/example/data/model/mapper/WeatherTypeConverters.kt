package com.example.data.model.mapper


import androidx.room.TypeConverter
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.google.gson.Gson

class WeatherTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromCurrentWeather(data: CurrentWeatherDto?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toCurrentWeather(json: String?): CurrentWeatherDto? {
        return gson.fromJson(json, CurrentWeatherDto::class.java)
    }

    @TypeConverter
    fun fromHourlyForecast(data: HourlyForecastResponse?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toHourlyForecast(json: String?): HourlyForecastResponse? {
        return gson.fromJson(json, HourlyForecastResponse::class.java)
    }

    @TypeConverter
    fun fromFiveDayForecast(data: FiveDayForecastResponse?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toFiveDayForecast(json: String?): FiveDayForecastResponse? {
        return gson.fromJson(json, FiveDayForecastResponse::class.java)
    }
}
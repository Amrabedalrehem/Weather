package com.example.data.datasource.remote

import com.example.data.model.weather.CurrentWeatherDto
import com.example.data.model.weather.HourlyForecastResponse
import com.example.data.network.RetrofitHelper
import retrofit2.Response

class DataSourceRemote() {
    val api = RetrofitHelper.retrofitService
    suspend fun getCurrentWeather( lat: Double, lon: Double,  lang: String, units: String): Response<CurrentWeatherDto>
    {
        return   api.getCurrentWeather(
            lat = lat,
            lon = lon,
            lang = lang,
            units = units
        )
    }

    suspend fun getHourlyForecast(city: String): Response<HourlyForecastResponse> {
        return api.getHourlyForecast(city)
    }
}
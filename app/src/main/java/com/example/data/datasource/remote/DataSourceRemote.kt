package com.example.data.datasource.remote

import com.example.data.model.weather.WeatherDto
import com.example.data.network.RetrofitHelper
import retrofit2.Response

class DataSourceRemote() {
    val api = RetrofitHelper.retrofitService
    suspend fun getCurrentWeather( lat: Double, lon: Double,  lang: String, units: String): Response<WeatherDto>
    {
        return   api.getCurrentWeather(
            lat = lat,
            lon = lon,
            lang = lang,
            units = units
        )
    }
}
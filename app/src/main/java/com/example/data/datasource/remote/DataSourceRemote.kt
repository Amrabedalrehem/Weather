package com.example.data.datasource.remote

import com.example.data.model.weather.CurrentWeatherDto
import com.example.data.model.weather.FiveDayForecastResponse
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

    suspend fun getHourlyForecast(city: String, units : String, lang: String ): Response<HourlyForecastResponse> {
        return api.getHourlyForecast(city = city,units= units, lang = lang)
    }

    suspend fun getFiveDayForecast(city: String,lat: Double,lon: Double,units : String, lang: String ): Response<FiveDayForecastResponse> {
        return api.getFiveDayForecast(
            city = city,units= units, lat = lat, lon = lon, lang = lang)
    }

}

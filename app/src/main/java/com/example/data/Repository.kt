package com.example.data

 import com.example.data.datasource.local.DataSourceLocal
 import com.example.data.datasource.remote.DataSourceRemote
import com.example.data.model.weather.CurrentWeatherDto
 import com.example.data.model.weather.HourlyForecastResponse
 import retrofit2.Response

class Repository (
    private val local: DataSourceLocal,
    private val remote :DataSourceRemote

){
     suspend fun getCurrentWeather( lat: Double, lon: Double,   lang: String, units: String): Response<CurrentWeatherDto>
    {
      return  remote.getCurrentWeather(
            lat = lat,
            lon = lon,
            lang = lang,
            units = units
        )
    }

    suspend fun getHourlyForecast(city: String): Response<HourlyForecastResponse> {
        return remote.getHourlyForecast(city)
    }
}
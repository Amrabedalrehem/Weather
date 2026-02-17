package com.example.data

 import com.example.data.datasource.local.DataSourceLocal
 import com.example.data.datasource.remote.DataSourceRemote
import com.example.data.model.weather.WeatherDto
import retrofit2.Response

class Repository (
    private val local: DataSourceLocal,
    private val remote :DataSourceRemote

){
     suspend fun getCurrentWeather( lat: Double, lon: Double,   lang: String, units: String): Response<WeatherDto>
    {
      return  remote.getCurrentWeather(
            lat = lat,
            lon = lon,
            lang = lang,
            units = units
        )
    }
}
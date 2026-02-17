package com.example.data.datasource.remote

import com.example.data.model.weather.WeatherDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val KEY_ = "a50b3547c713e7be1ec57c696006497f"

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = KEY_,
        @Query("lang") lang: String = "en",
        @Query("units") units: String = "metric"
    ): Response<WeatherDto>


}

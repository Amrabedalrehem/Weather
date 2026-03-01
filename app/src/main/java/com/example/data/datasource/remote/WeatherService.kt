package com.example.data.datasource.remote

import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
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
        @Query("lang") lang: String,
        @Query("units") units: String
    ): Response<CurrentWeatherDto>


    @GET("data/2.5/forecast/hourly")
    suspend fun getHourlyForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String = KEY_,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("cnt") count: Int = 24
    ):Response<HourlyForecastResponse>

    @GET("data/2.5/forecast/climate")
    suspend fun getFiveDayForecast(
        @Query("appid") apiKey: String = KEY_,
        @Query("q") city: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String ,
        @Query("cnt") count: Int = 6
    ): Response<FiveDayForecastResponse>


}

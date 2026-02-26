package com.example.data.datasource.remote
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse

import retrofit2.Response;


interface IDataSourceRemote {
    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, units: String): Response<CurrentWeatherDto>
    suspend fun getHourlyForecast(city: String, units: String, lang: String): Response<HourlyForecastResponse>
    suspend fun getFiveDayForecast(city: String, lat: Double, lon: Double, units: String, lang: String): Response<FiveDayForecastResponse>
}
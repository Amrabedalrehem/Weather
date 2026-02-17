package com.example.data.network

import com.example.data.datasource.remote.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    const val BASE_URL = "https://api.openweathermap.org/"

    private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL).build()

    val retrofitService: WeatherService = retrofit.create(WeatherService::class.java)

}
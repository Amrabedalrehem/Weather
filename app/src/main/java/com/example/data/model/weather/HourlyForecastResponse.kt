package com.example.data.model.weather

import com.google.gson.annotations.SerializedName

data class HourlyForecastResponse(
    @SerializedName("list")
    val hourly: List<HourlyItemDto>,

    @SerializedName("city")
    val city: City
)

data class City(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String
)

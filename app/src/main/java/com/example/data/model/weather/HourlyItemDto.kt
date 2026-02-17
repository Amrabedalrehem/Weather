package com.example.data.model.weather

import com.google.gson.annotations.SerializedName

data class HourlyItemDto(
    @SerializedName("dt_txt")
    val time: String,

    @SerializedName("main")
    val main: Main,

    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("weather")
    val weather: List<Weather>
)



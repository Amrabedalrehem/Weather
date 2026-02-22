package com.example.data.model.dto

import com.google.gson.annotations.SerializedName

data class FiveDayForecastDTo(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("clouds")
    val clouds: Int,

    @SerializedName("weather")
    val weather: List<Weather>,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("pressure")
    val pressure: Int,

    @SerializedName("speed")
    val speed: Double,

    @SerializedName("temp")
    val temp: Temp
)

data class  Temp (
    @SerializedName("day")
   val day: Double,
    @SerializedName("night")
    val night :Double,
    @SerializedName("eve")
    val eve :Double,
    @SerializedName("morn")
    val morn :Double,
    @SerializedName("min")
    val min :Double,
    @SerializedName("max")
    val max :Double
)


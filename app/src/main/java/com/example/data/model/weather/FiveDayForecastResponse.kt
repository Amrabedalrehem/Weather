package com.example.data.model.weather

import com.google.gson.annotations.SerializedName

data class FiveDayForecastResponse(

  @SerializedName("list")
  val fiveDay: List<FiveDayForecastDTo>
)

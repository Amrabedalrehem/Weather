package com.example.data.model.dto

import com.google.gson.annotations.SerializedName

data class FiveDayForecastResponse(

  @SerializedName("list")
  val fiveDay: List<FiveDayForecastDTo>
)

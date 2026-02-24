package com.example.presentation.component.helper


sealed class WeatherAlertState {
    object Loading : WeatherAlertState()
    data class Success(
        val temp: Int,
        val description: String,
        val feelsLike: Int
    ) : WeatherAlertState()
    object Error : WeatherAlertState()
}
package com.example.presentation.utils

import com.example.weather.R


fun getWeatherIcon(iconCode: String?): Int {
    return when (iconCode) {
        "01d" -> R.drawable.sunny
        "01n" -> R.drawable.clear_night
        "02d" -> R.drawable.partly_cloudy
        "02n" -> R.drawable.partly_cloudy_night
        "03d", "03n" -> R.drawable.cloudy
        "04d", "04n" -> R.drawable.overcast
        "09d", "09n" -> R.drawable.drizzle
        "10d" -> R.drawable.rainy_day
        "10n" -> R.drawable.rainy_night
        "11d", "11n" -> R.drawable.thunderstorm
        "13d", "13n" -> R.drawable.snowy
        "50d", "50n" -> R.drawable.foggy
        else -> R.drawable.sunny
    }
}


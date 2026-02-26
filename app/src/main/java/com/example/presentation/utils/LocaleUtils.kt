package com.example.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.weather.R
import java.util.Locale
import kotlin.text.iterator


fun String.toArabicDigits(): String {
    if (Locale.getDefault().language != "ar") return this
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return buildString {
        for (c in this@toArabicDigits) {
            append(if (c in '0'..'9') arabicDigits[c - '0'] else c)
        }
    }
}

@Composable
fun localizeWeatherMain(main: String): String = when (main) {
    "Clear" -> stringResource(R.string.weather_clear)
    "Clouds" -> stringResource(R.string.weather_clouds)
    "Rain" -> stringResource(R.string.weather_rain)
    "Drizzle" -> stringResource(R.string.weather_drizzle)
    "Thunderstorm" -> stringResource(R.string.weather_thunderstorm)
    "Snow" -> stringResource(R.string.weather_snow)
    "Mist" -> stringResource(R.string.weather_mist)
    "Haze" -> stringResource(R.string.weather_haze)
    "Fog" -> stringResource(R.string.weather_fog)
    "Dust" -> stringResource(R.string.weather_dust)
    "Sand" -> stringResource(R.string.weather_sand)
    "Smoke" -> stringResource(R.string.weather_smoke)
    "Tornado" -> stringResource(R.string.weather_tornado)
    "Squall" -> stringResource(R.string.weather_squall)
    else -> main
}

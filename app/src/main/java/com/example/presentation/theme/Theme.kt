package com.example.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = WeatherBlueMid,
    onPrimary = Color.White,
    secondary = WeatherBlueDark,
    tertiary = WeatherCyan,
    background = LightBackground,
    surface = Color.White,
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E)
)

private val DarkColorScheme = darkColorScheme(
    primary = WeatherBlueLight,
    onPrimary = Color(0xFF003355),
    secondary = WeatherBlueMid,
    tertiary = WeatherCyan,
    background = DarkBackground,
    surface = Color(0xFF1B2838),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6)
)

 val LocalWeatherGradient = staticCompositionLocalOf { LightGradient }

@Composable
fun WeatherTheme(
    themeMode: String = "system",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val gradient = if (darkTheme) DarkGradient else LightGradient

    CompositionLocalProvider(LocalWeatherGradient provides gradient) {
        MaterialTheme(
            colorScheme = colorScheme,
             typography = MaterialTheme.typography,
            content = content
        )
    }
}
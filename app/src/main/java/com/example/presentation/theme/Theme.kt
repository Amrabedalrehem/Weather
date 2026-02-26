package com.example.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WeatherBlueLight,
    secondary = WeatherBlueMid,
    tertiary = WeatherCyan,
    background = Color(0xFF0D1B2A),
    surface = Color(0xFF1B2838)
)

private val LightColorScheme = lightColorScheme(
    primary = WeatherBlueMid,
    secondary = WeatherBlueDark,
    tertiary = WeatherCyan,
    background = Color(0xFF2196F3),
    surface = Color(0xFF03A9F4)
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
            typography = Typography,
            content = content
        )
    }
}
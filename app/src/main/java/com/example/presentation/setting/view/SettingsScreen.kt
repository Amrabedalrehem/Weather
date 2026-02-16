package com.example.presentation.setting.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weather.R

@Composable
fun SettingsScreen(modifier: Modifier) {

    var temperature by remember { mutableStateOf("Celsius (°C)") }
    var location by remember { mutableStateOf("Gps") }
    var language by remember { mutableStateOf("English") }
    var windSpeed by remember { mutableStateOf("m/s") }
    var theme by remember { mutableStateOf("System") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B8CB5),
                        Color(0xFF8BA5C9),
                        Color(0xFF9FB5D1)
                    )
                )
            )
            .padding(vertical = 12.dp),

        )
    {
        SettingsCard(
            title = "Language",
            icon = R.drawable.languages,
            options = listOf("Default", "English", "العربية"),
            selectedOption = language,
            onOptionSelected = { select -> language = select }
        )

        SettingsCard(
            title = "Temperature Unit",
            icon = R.drawable.temperature,
            options = listOf("Celsius (°C)", "kelvin (°K)", "Fahrenheit (°F)"),
            selectedOption = temperature,
            onOptionSelected = { select ->
                temperature = select
            }
        )

        SettingsCard(
            title = "Location",
            icon = R.drawable.map,
            options = listOf("Gps", "Map"),
            selectedOption = location,
            onOptionSelected = { select -> location = select }
        )


        SettingsCard(
            title = "Wind Speed Unit",
            icon = R.drawable.wind,
            options = listOf("m/s", "mph"),
            selectedOption = windSpeed,
            onOptionSelected = { select -> windSpeed = select }
        )

        SettingsCard(
            title = "Theme",
            icon = R.drawable.color,
            options = listOf("System", "Dark", "Light"),
            selectedOption = theme,
            onOptionSelected = { select -> theme = select }
        )
    }
}




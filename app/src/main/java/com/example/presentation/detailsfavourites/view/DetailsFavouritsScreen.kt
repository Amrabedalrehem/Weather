package com.example.presentation.detailsfavourites.view

import com.example.presentation.component.home.FiveDayForecastSection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
 import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.component.home.CurrentWeatherSection
import com.example.presentation.component.home.HourlyForecastSection
import com.example.presentation.component.home.WeatherDetailsGrid
import com.example.presentation.detailsfavourites.viewmodel.DetailsFavoritesViewModel

@Composable
fun DetailsFavoritesScreen(modifier: Modifier, locationId: Int,   viewModel: DetailsFavoritesViewModel)
{

    val item by viewModel.itemFavourite.collectAsStateWithLifecycle()
    val windUnit by viewModel.windSpeedUnit.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = com.example.presentation.theme.LocalWeatherGradient.current
                )
            )
    ) {
    LazyColumn {
        item { CurrentWeatherSection(item?.currentWeather) }
        item { Spacer(Modifier.height(16.dp)) }
        item { WeatherDetailsGrid(item?.currentWeather, windUnit = windUnit) }
        item { Spacer(Modifier.height(16.dp)) }
        item { HourlyForecastSection(item?.hourlyForecast, windUnit = windUnit) }
        item { Spacer(Modifier.height(16.dp)) }
        item { FiveDayForecastSection(item?.fiveDayForecast, windUnit = windUnit) }
        item { Spacer(Modifier.height(16.dp)) }
        }}

}
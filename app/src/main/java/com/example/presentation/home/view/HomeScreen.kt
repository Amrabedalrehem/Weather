package com.example.presentation.home.view
import FiveDayForecastSection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.presentation.component.helper.ErrorState
import com.example.presentation.component.helper.LoadingState
import com.example.presentation.component.helper.UiState
import com.example.presentation.component.home.CurrentWeatherSection
import com.example.presentation.component.home.HourlyForecastSection
import com.example.presentation.component.home.WeatherDetailsGrid
import com.example.presentation.home.viewmodel.HomeViewModel
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val currentUiState by viewModel.currentWeather.collectAsState()
    val hourlyUiState by viewModel.hourlyForecast.collectAsState()
    val fiveDayUiState by viewModel.fiveDayForecast.collectAsState()
    val windUnit by viewModel.windSpeedUnit.collectAsState()


      if (currentUiState is UiState.Loading || hourlyUiState is UiState.Loading|| fiveDayUiState is UiState.Loading) {
        LoadingState()
        return
    }

     if (currentUiState is UiState.Error || hourlyUiState is UiState.Error|| fiveDayUiState is UiState.Error) {
         val isConnected by viewModel.isConnected.collectAsState()
         val errorMessage = (currentUiState as? UiState.Error)?.message
            ?: (hourlyUiState as? UiState.Error)?.message?: (fiveDayUiState as? UiState.Error)?.message
            ?: stringResource(R.string.unknown_error)
        ErrorState(
            errorMessage = errorMessage,
                 onRetry = {
                    if (isConnected) viewModel.getInfoWeather()
                      }
        )
        return
    }

     if (currentUiState is UiState.Success && hourlyUiState is UiState.Success) {
        val currentData = (currentUiState as UiState.Success).data
        val hourlyData = (hourlyUiState as UiState.Success).data
         val  fiveDayData = (fiveDayUiState as UiState.Success).data
        HomeScreenContent(
            weatherData = currentData,
            hourlyForecast = hourlyData,
            fiveDayData =fiveDayData,
            windUnit  = windUnit
        )
    }
}

@Composable
fun HomeScreenContent(weatherData: CurrentWeatherDto, hourlyForecast : HourlyForecastResponse, fiveDayData : FiveDayForecastResponse,modifier: Modifier = Modifier
                      ,  windUnit :String
)
{

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3),
                        Color(0xFF03A9F4),
                        Color(0xFF00BCD4)
                    )

                )
            )
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)

         ) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { -it }
                ) {
                    CurrentWeatherSection(weatherData,)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) +
                            slideInHorizontally(tween(800, delayMillis = 200)) { -it }
                ) {
                    WeatherDetailsGrid(weatherData
                    ,  windUnit = windUnit
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400))
                ) {
                    HourlyForecastSection(hourlyForecast,    windUnit = windUnit
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 600))
                ) {
                    FiveDayForecastSection(fiveDayData,    windUnit = windUnit
                    )
                }
            }
        }
    }
}







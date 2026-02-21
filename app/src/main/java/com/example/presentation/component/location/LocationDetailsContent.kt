package com.example.presentation.component.location

import FiveDayForecastSection
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.presentation.component.helper.ErrorState
import com.example.presentation.component.helper.LoadingState
import com.example.presentation.component.helper.UiState
import com.example.presentation.component.home.CurrentWeatherSection
import com.example.presentation.component.home.HourlyForecastSection
import com.example.presentation.component.home.WeatherDetailsGrid
import com.google.android.gms.maps.model.LatLng

@Composable
fun LocationDetailsContent(
    address: String,
    city: String,
    country: String,
    latLng: LatLng?,
    viewModel: MapPickerViewModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    windUnit :String
) {
    val currentUiState by viewModel.currentWeather.collectAsState()
    val hourlyUiState by viewModel.hourlyForecast.collectAsState()
    val fiveDayUiState by viewModel.fiveDayForecast.collectAsState()

    LaunchedEffect(latLng) {
        latLng?.let {
            viewModel.getWeatherByLocation(it.latitude, it.longitude)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {val apiCityName = (currentUiState as? UiState.Success)?.data?.name

        Text(
            text = apiCityName ?: city.ifEmpty { address },
                style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

        when {
            currentUiState is UiState.Loading -> LoadingState()

            currentUiState is UiState.Error -> {
                val msg = (currentUiState as UiState.Error).message
                ErrorState(
                    errorMessage = msg,
                    onRetry = {
                        latLng?.let { viewModel.getWeatherByLocation(it.latitude, it.longitude) }
                    }
                )
            }

            currentUiState is UiState.Success &&
                    hourlyUiState is UiState.Success &&
                    fiveDayUiState is UiState.Success -> {
                val currentData = (currentUiState as UiState.Success).data
                val hourlyData = (hourlyUiState as UiState.Success).data
                val fiveDayData = (fiveDayUiState as UiState.Success).data

                LazyColumn {
                    item { CurrentWeatherSection(currentData) }
                    item { Spacer(Modifier.height(16.dp)) }
                    item { WeatherDetailsGrid(currentData,windUnit = windUnit) }
                    item { Spacer(Modifier.height(16.dp)) }
                    item { HourlyForecastSection(hourlyData,windUnit = windUnit) }
                    item { Spacer(Modifier.height(16.dp)) }
                    item { FiveDayForecastSection(fiveDayData,windUnit = windUnit) }
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, Color.White),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = onConfirm,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90D9))
                            ) {
                                Text("Confirm Location")
                            }
                        }
                    }
                }
            }
        }
    }
}

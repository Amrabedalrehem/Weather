package com.example.presentation.alart.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.presentation.alart.viewmodel.AlertViewModel
import com.example.presentation.component.helper.WeatherAlertState
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import com.example.presentation.component.helper.toArabicDigits


@Composable
fun AlertScreen(
    city: String,
    viewModel: AlertViewModel,
    onDismiss: () -> Unit,
    onSnooze: (Int) -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchWeather() }

    Column(
        modifier            = Modifier.fillMaxSize().background(Color(0xFF1976D2)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.notification))
        LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(130.dp))

        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(R.string.weather_alert), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text(city, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3B82F6))
        Spacer(modifier = Modifier.height(24.dp))

        when (val state = weatherState) {
            is WeatherAlertState.Loading -> {
                CircularProgressIndicator(color = Color(0xFF3B82F6))
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.fetching_weather), color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            is WeatherAlertState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${state.temp}°".toArabicDigits(), fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(state.description.replaceFirstChar { it.uppercase() }, fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${stringResource(R.string.feels_like, state.feelsLike.toString())}°".toArabicDigits(), fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
            is WeatherAlertState.Error -> {
                Text(stringResource(R.string.could_not_load_weather), color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick  = { onSnooze(10) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3B82F6))
        ) {
            Text(stringResource(R.string.snooze_10_min), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick  = onDismiss,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.dismiss), fontSize = 18.sp, color = Color.White)
        }
    }
}
package com.example.presentation.alart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.presentation.component.helper.WeatherAlertState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(
    application: Application,
    private val lat: Double,
    private val lon: Double,
    private val repository: Repository
) : AndroidViewModel(application) {

    private val _weatherState = MutableStateFlow<WeatherAlertState>(WeatherAlertState.Loading)
    val weatherState: StateFlow<WeatherAlertState> = _weatherState

    fun fetchWeather() {
        viewModelScope.launch {
            _weatherState.value = WeatherAlertState.Loading
            try {
                val response = repository.getCurrentWeather(lat, lon)
                if (response.isSuccessful) {
                    val weather = response.body()
                    _weatherState.value = WeatherAlertState.Success(
                        temp        = weather?.main?.temp?.toInt()                 ?: 0,
                        description = weather?.weather?.firstOrNull()?.description ?: "",
                        feelsLike   = weather?.main?.feelsLike?.toInt()            ?: 0
                    )
                } else {
                    _weatherState.value = WeatherAlertState.Error
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherAlertState.Error
            }
        }
    }
}
package com.example.presentation.alart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ApiResult
import com.example.data.IRepository
import com.example.presentation.utils.WeatherAlertState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class AlertViewModel(
    application: Application,
    private val lat: Double,
    private val lon: Double,
    private val repository: IRepository
) : AndroidViewModel(application) {

    private val _weatherState = MutableStateFlow<WeatherAlertState>(WeatherAlertState.Loading)
    val weatherState: StateFlow<WeatherAlertState> = _weatherState

    fun fetchWeather() {
        viewModelScope.launch {
            repository.getCurrentWeather(lat, lon).collect { result ->
                when (result) {
                    is ApiResult.Loading -> _weatherState.value = WeatherAlertState.Loading
                    is ApiResult.Success -> _weatherState.value = WeatherAlertState.Success(
                        temp        = result.data.main?.temp?.toInt()                 ?: 0,
                        description = result.data.weather?.firstOrNull()?.description ?: "",
                        feelsLike   = result.data.main?.feelsLike?.toInt()            ?: 0
                    )
                    is ApiResult.Error -> _weatherState.value = WeatherAlertState.Error
                }
            }
        }
    }
}
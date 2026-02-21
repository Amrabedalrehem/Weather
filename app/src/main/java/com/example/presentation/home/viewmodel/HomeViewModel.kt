package com.example.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.weather.CurrentWeatherDto
import com.example.data.model.weather.FiveDayForecastResponse
import com.example.data.model.weather.HourlyForecastResponse
import com.example.presentation.component.helper.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class HomeViewModel(val repository: Repository) : ViewModel() {
    private var _currentWeather = MutableStateFlow<UiState<CurrentWeatherDto>>(UiState.Loading)
    val currentWeather: StateFlow<UiState<CurrentWeatherDto>> = _currentWeather

    private var _hourlyForecast = MutableStateFlow<UiState<HourlyForecastResponse>>(UiState.Loading)
    val hourlyForecast: StateFlow<UiState<HourlyForecastResponse>> = _hourlyForecast

    private var _fiveDayForecast =
        MutableStateFlow<UiState<FiveDayForecastResponse>>(UiState.Loading)
    val fiveDayForecast: StateFlow<UiState<FiveDayForecastResponse>> = _fiveDayForecast

    val handleCurrentWeatherException = CoroutineExceptionHandler { _, exception ->
        _currentWeather.value = UiState.Error(exception.message.toString())
    }
    val handleHourlyException = CoroutineExceptionHandler { _, exception ->
        _hourlyForecast.value = UiState.Error(exception.message.toString())
    }
    val handleFiveDayException = CoroutineExceptionHandler { _, exception ->
        _fiveDayForecast.value = UiState.Error(exception.message.toString())
    }

    fun getInfoWeather() {
        _currentWeather.value = UiState.Loading
        viewModelScope.launch {
             val responseCurrentWeather = repository.getCurrentWeather()
            if (responseCurrentWeather.isSuccessful) {
                val data = responseCurrentWeather.body()
                if (data != null) _currentWeather.value = UiState.Success(data)
                else _currentWeather.value = UiState.Error("No Data")
            } else {
                _currentWeather.value = UiState.Error("Error ${responseCurrentWeather.code()}")
            }

            val cityName = (_currentWeather.value as? UiState.Success)?.data?.name ?: "Cairo"


            _hourlyForecast.value = UiState.Loading
            val responseHourForecast = repository.getHourlyForecast(cityName)
            if (responseHourForecast.isSuccessful) {
                val data = responseHourForecast.body()
                if (data != null) _hourlyForecast.value = UiState.Success(data)
                else _hourlyForecast.value = UiState.Error("No Data")
            } else {
                _hourlyForecast.value = UiState.Error("Error ${responseHourForecast.code()}")
            }

            _fiveDayForecast.value = UiState.Loading
            val response = repository.getFiveDayForecast(cityName)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) _fiveDayForecast.value = UiState.Success(data)
                else _fiveDayForecast.value = UiState.Error("No Data")
            } else {
                _fiveDayForecast.value = UiState.Error("Error ${response.code()}")
            }

        }
    }

    init {
        viewModelScope.launch {
            getInfoWeather()

            combine(
                repository.language,
                repository.temperatureUnit
            ) { lang, units ->
                Pair(lang, units)
            }.drop(1)
                .collectLatest {
                    getInfoWeather()
                }
        }
    }
    }
class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
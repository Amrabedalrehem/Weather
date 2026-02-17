package com.example.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.weather.CurrentWeatherDto
import com.example.data.model.weather.FiveDayForecastResponse
import com.example.data.model.weather.HourlyForecastResponse
import com.example.presentation.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.joinAll
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

        viewModelScope.launch {

            val current = launch(handleCurrentWeatherException) {
                _currentWeather.value = UiState.Loading
                val response = repository.getCurrentWeather(
                    lat = 30.599405,
                    lon = 31.489460,
                    lang = "en",
                    units = "metric",
                )
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data != null) {
                        _currentWeather.value = UiState.Success(data)
                    } else {
                        _currentWeather.value = UiState.Error("No Data")
                    }

                } else {
                    _currentWeather.value = UiState.Error("Error ${response.code()}")
                }


            }
            current.join()

            val cityName = (_currentWeather.value as? UiState.Success)?.data?.name ?: "Cairo"

            val hourly = launch(handleHourlyException) {
                _hourlyForecast.value = UiState.Loading
                val response = repository.getHourlyForecast(cityName, "en", "metric")

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _hourlyForecast.value = UiState.Success(data)
                    } else {
                        _hourlyForecast.value = UiState.Error("No Data")
                    }
                } else {
                    _hourlyForecast.value = UiState.Error("Error ${response.code()}")
                }

            }

            val fiveDay = launch(handleFiveDayException) {

                _fiveDayForecast.value = UiState.Loading
                val response = repository.getFiveDayForecast(
                    cityName,
                    lat = 30.599405,
                    lon = 31.489460,
                    "en", "metric"
                )

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _fiveDayForecast.value = UiState.Success(data)
                    } else {
                        _fiveDayForecast.value = UiState.Error("No Data")
                    }
                } else {
                    _fiveDayForecast.value = UiState.Error("Error ${response.code()}")
                }
            }

            joinAll(hourly, fiveDay)
        }
    }


    init {
        getInfoWeather()
    }
}

class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
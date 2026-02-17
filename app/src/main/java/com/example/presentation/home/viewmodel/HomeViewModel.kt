package com.example.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.weather.WeatherDto
import com.example.presentation.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(val repository: Repository) : ViewModel() {
   private var _currentWeather = MutableStateFlow<UiState<WeatherDto>>(UiState.Loading)
    val currentWeather: StateFlow<UiState<WeatherDto>> = _currentWeather
    val handleExpectation = CoroutineExceptionHandler { context, exception ->
        _currentWeather.value = UiState.Error(exception.message.toString())
    }
    fun getCurrentWeather() {
        viewModelScope.launch(handleExpectation) {
            _currentWeather.value = UiState.Loading
            val response = repository.getCurrentWeather(
                lat = 31.4895,
                lon = 30.5994,
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
    }
init {
    getCurrentWeather()
}
}

class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
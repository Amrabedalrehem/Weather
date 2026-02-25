package com.example.presentation.home.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.HomeWeatherCache
import com.example.data.network.CheckNetwork
import com.example.presentation.component.helper.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    val repository: Repository,
    private val networkObserver: CheckNetwork
) : ViewModel() {

    private var _currentWeather = MutableStateFlow<UiState<CurrentWeatherDto>>(UiState.Loading)
    val currentWeather: StateFlow<UiState<CurrentWeatherDto>> = _currentWeather

    private var _hourlyForecast = MutableStateFlow<UiState<HourlyForecastResponse>>(UiState.Loading)
    val hourlyForecast: StateFlow<UiState<HourlyForecastResponse>> = _hourlyForecast

    private var _fiveDayForecast = MutableStateFlow<UiState<FiveDayForecastResponse>>(UiState.Loading)
    val fiveDayForecast: StateFlow<UiState<FiveDayForecastResponse>> = _fiveDayForecast

    val windSpeedUnit: StateFlow<String> = repository.windSpeedUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = "m/s"
        )
    val isConnected: StateFlow<Boolean> = networkObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    fun getInfoWeather() {
        viewModelScope.launch {
            val isConnected = networkObserver.isConnected.first()

            if (!isConnected) {
                 val cache = repository.getHomeWeather().first()
                if (cache != null) {
                    cache.currentWeather?.let { _currentWeather.value = UiState.Success(it) }
                    cache.hourlyForecast?.let { _hourlyForecast.value = UiState.Success(it) }
                    cache.fiveDayForecast?.let { _fiveDayForecast.value = UiState.Success(it) }
                } else {
                    _currentWeather.value = UiState.Error("No internet & No cached data")
                    _hourlyForecast.value = UiState.Error("No internet & No cached data")
                    _fiveDayForecast.value = UiState.Error("No internet & No cached data")
                }
                return@launch
            }
            _currentWeather.value = UiState.Loading
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
            val current = (_currentWeather.value as? UiState.Success)?.data
            val hourly = (_hourlyForecast.value as? UiState.Success)?.data
            val fiveDay = (_fiveDayForecast.value as? UiState.Success)?.data

            if (current != null && hourly != null && fiveDay != null) {
                repository.insertHomeWeather(
                    HomeWeatherCache(
                        currentWeather = current,
                        hourlyForecast = hourly,
                        fiveDayForecast = fiveDay
                    )
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            getInfoWeather()

             combine(
                repository.language,
                repository.temperatureUnit,
                repository.latitude,
                repository.longitude
            ) { lang, units, lat, lon ->
                Triple(lang, units, Pair(lat, lon))
            }.drop(1)
                .collectLatest { getInfoWeather() }
        }

         viewModelScope.launch {
            networkObserver.isConnected
                .drop(1)
                .collectLatest { isConnected ->
                    if (isConnected) getInfoWeather()
                }
        }
    }
}

class HomeViewModelFactory(
    private val repository: Repository,
    private val networkObserver: CheckNetwork
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, networkObserver) as T
    }
}
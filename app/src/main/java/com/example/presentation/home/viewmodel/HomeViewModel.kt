package com.example.presentation.home.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ApiResult
import com.example.data.IRepository
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.HomeWeatherCache
import com.example.presentation.utils.CheckNetwork
import com.example.presentation.utils.UiState
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
    val repository: IRepository,
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
            initialValue = "ms"
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
                loadFromCache()
                return@launch
            }
            fetchCurrentWeather()
            val cityName = (_currentWeather.value as? UiState.Success)?.data?.name ?: "Cairo"
            fetchHourlyForecast(cityName)
            fetchFiveDayForecast(cityName)
            cacheWeatherData()
        }
    }

    private suspend fun loadFromCache() {
        val cache = repository.getHomeWeather().first()
        if (cache != null) {
            cache.currentWeather?.let { _currentWeather.value = UiState.Success(it) }
            cache.hourlyForecast?.let { _hourlyForecast.value = UiState.Success(it) }
            cache.fiveDayForecast?.let { _fiveDayForecast.value = UiState.Success(it) }
        } else {
            val error = UiState.Error("No internet & No cached data")
            _currentWeather.value  = error
            _hourlyForecast.value  = error
            _fiveDayForecast.value = error
        }
    }

    private suspend fun fetchCurrentWeather() {
        repository.getCurrentWeather().collect { result ->
            when (result) {
                is ApiResult.Loading -> _currentWeather.value = UiState.Loading
                is ApiResult.Success -> _currentWeather.value = UiState.Success(result.data)
                is ApiResult.Error   -> _currentWeather.value = UiState.Error(result.message)
            }
        }
    }

    private suspend fun fetchHourlyForecast(cityName: String) {
        repository.getHourlyForecast(cityName).collect { result ->
            when (result) {
                is ApiResult.Loading -> _hourlyForecast.value = UiState.Loading
                is ApiResult.Success -> _hourlyForecast.value = UiState.Success(result.data)
                is ApiResult.Error   -> _hourlyForecast.value = UiState.Error(result.message)
            }
        }
    }

    private suspend fun fetchFiveDayForecast(cityName: String) {
        repository.getFiveDayForecast(cityName).collect { result ->
            when (result) {
                is ApiResult.Loading -> _fiveDayForecast.value = UiState.Loading
                is ApiResult.Success -> _fiveDayForecast.value = UiState.Success(result.data)
                is ApiResult.Error   -> _fiveDayForecast.value = UiState.Error(result.message)
            }
        }
    }

    private suspend fun cacheWeatherData() {
        val current = (_currentWeather.value as? UiState.Success)?.data
        val hourly  = (_hourlyForecast.value as? UiState.Success)?.data
        val fiveDay = (_fiveDayForecast.value as? UiState.Success)?.data

        if (current != null && hourly != null && fiveDay != null) {
            repository.insertHomeWeather(
                HomeWeatherCache(
                    currentWeather  = current,
                    hourlyForecast  = hourly,
                    fiveDayForecast = fiveDay
                )
            )
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
    private val repository: IRepository,
    private val networkObserver: CheckNetwork
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, networkObserver) as T
    }
}
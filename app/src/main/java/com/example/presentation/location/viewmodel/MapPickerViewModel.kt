package com.example.presentation.component.location
import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ApiResult
import com.example.data.IRepository
import com.example.data.Repository
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.FavouriteLocationCache
import com.example.presentation.utils.CheckNetwork
import com.example.presentation.utils.UiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class MapPickerViewModel(val repository: IRepository, private val networkObserver: CheckNetwork
) : ViewModel() {
    var defaultLocation by mutableStateOf(LatLng(30.0444, 31.2357))
        private set
    var isLocationLoaded by mutableStateOf(false)
        private set

    val isConnected: StateFlow<Boolean> = networkObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    val windSpeedUnit: StateFlow<String> = repository.windSpeedUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = "ms"
        )
    init {
        viewModelScope.launch {
            val lat = repository.latitude.first()
            val lon = repository.longitude.first()
            defaultLocation = LatLng(lat, lon)
            isLocationLoaded = true
        }
    }
    var selectedLocation by mutableStateOf<LatLng?>(null)
        private set
    var selectedAddress by mutableStateOf("")
        private set
    var selectedCity by mutableStateOf("")
        private set
    var selectedCountry by mutableStateOf("")
        private set

    private val _currentWeather = MutableStateFlow<UiState<CurrentWeatherDto>>(UiState.Loading)
    val currentWeather: StateFlow<UiState<CurrentWeatherDto>> = _currentWeather

    private val _hourlyForecast = MutableStateFlow<UiState<HourlyForecastResponse>>(UiState.Loading)
    val hourlyForecast: StateFlow<UiState<HourlyForecastResponse>> = _hourlyForecast

    private val _fiveDayForecast = MutableStateFlow<UiState<FiveDayForecastResponse>>(UiState.Loading)
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

    fun onMapClick(latLng: LatLng, context: Context) {
        selectedLocation = latLng
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val address = addresses?.firstOrNull()
        selectedAddress = address?.getAddressLine(0) ?: ""
        selectedCity = address?.locality ?: address?.subAdminArea ?: ""
        selectedCountry = address?.countryName ?: ""
    }

    fun onPlaceSelected(latLng: LatLng, address: String) {
        selectedLocation = latLng
        selectedAddress = address
    }
    fun saveLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.saveLocation(lat, lon)
        }
    }
    fun getWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            fetchCurrentWeather(lat, lon)
            val cityName = (_currentWeather.value as? UiState.Success)?.data?.name ?: "Cairo"
            fetchHourlyForecast(cityName)
            fetchFiveDayForecast(cityName)
        }
    }

    private suspend fun fetchCurrentWeather(lat: Double, lon: Double) {
        repository.getCurrentWeather(lat, lon).collect { result ->
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
    fun addFavourite(
        city: String,
        country: String,
        lat: Double,
        lon: Double,
        onSuccess: (FavouriteLocationCache) -> Unit
    ) {
        viewModelScope.launch {
             val currentWeatherData = (_currentWeather.value as? UiState.Success)?.data
            val hourlyData = (_hourlyForecast.value as? UiState.Success)?.data
            val fiveDayData = (_fiveDayForecast.value as? UiState.Success)?.data

            val newLocation = FavouriteLocationCache(
                city = city,
                country = country,
                lat = lat,
                lon = lon,
                currentWeather = currentWeatherData,
                hourlyForecast = hourlyData,
                fiveDayForecast = fiveDayData
            )

            repository.insert(newLocation)

            val savedLocation = repository.getAllFavourites().first().find {
                it.lat == lat && it.lon == lon
            }

            savedLocation?.let { onSuccess(it) }
        }
    }
    fun deleteFavourite(location: FavouriteLocationCache) {
        viewModelScope.launch {
            repository.delete(location)
        }
    }


}

class MapPickerViewModelFactory(private val repository: IRepository,
                                private val networkObserver: CheckNetwork
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapPickerViewModel(repository, networkObserver) as T
    }
}
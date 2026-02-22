package com.example.presentation.component.location
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.FavouriteLocation
import com.example.presentation.component.helper.UiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class MapPickerViewModel(val repository: Repository) : ViewModel() {
    var defaultLocation by mutableStateOf(LatLng(30.0444, 31.2357))
        private set
    var isLocationLoaded by mutableStateOf(false)
        private set
    val windSpeedUnit: StateFlow<String> = repository.windSpeedUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = "m/s"
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
            _currentWeather.value = UiState.Loading
            val responseFromCurrent = repository.getCurrentWeather(lat, lon)
            if (responseFromCurrent.isSuccessful) {
                val data = responseFromCurrent.body()
                if (data != null) _currentWeather.value = UiState.Success(data)
                else _currentWeather.value = UiState.Error("No Data")
            } else {
                _currentWeather.value = UiState.Error("Error ${responseFromCurrent.code()}")
            }

            val cityName = (_currentWeather.value as? UiState.Success)?.data?.name ?: "Cairo"

            _hourlyForecast.value = UiState.Loading
            val responseFromHourlyForecast = repository.getHourlyForecast(cityName)
            if (responseFromHourlyForecast.isSuccessful) {
                val data = responseFromHourlyForecast.body()
                if (data != null) _hourlyForecast.value = UiState.Success(data)
                else _hourlyForecast.value = UiState.Error("No Data")
            } else {
                _hourlyForecast.value = UiState.Error("Error ${responseFromHourlyForecast.code()}")
            }

            _fiveDayForecast.value = UiState.Loading
            val responseFromDayForecast = repository.getFiveDayForecast(cityName)
            if (responseFromDayForecast.isSuccessful) {
                val data = responseFromDayForecast.body()
                if (data != null) _fiveDayForecast.value = UiState.Success(data)
                else _fiveDayForecast.value = UiState.Error("No Data")
            } else {
                _fiveDayForecast.value = UiState.Error("Error ${responseFromDayForecast.code()}")
            }
        }}
    fun addFavourite(city: String, country: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.insert(FavouriteLocation(city = city, country = country, lat = lat, lon = lon))
        }
    }

    fun deleteFavourite(city: String, country: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.delete(FavouriteLocation(city = city, country = country, lat = lat, lon = lon))
        }
    }


}

class MapPickerViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapPickerViewModel(repository) as T
    }
}
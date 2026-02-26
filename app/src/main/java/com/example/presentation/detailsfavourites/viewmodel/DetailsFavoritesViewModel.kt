package com.example.presentation.detailsfavourites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.entity.FavouriteLocationCache
import com.example.presentation.utils.CheckNetwork
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsFavoritesViewModel(
    val repository: Repository,
    private val locationId: Int,
    private val networkObserver: CheckNetwork
) : ViewModel() {

    val itemFavourite: StateFlow<FavouriteLocationCache?> = repository.getFavouriteById(locationId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            fetchAndUpdate()
        }

        viewModelScope.launch {
            combine(
                repository.language,
                repository.temperatureUnit
            ) { lang, units -> }
                .drop(1)
                .collectLatest { fetchAndUpdate() }
        }

        viewModelScope.launch {
            networkObserver.isConnected
                .drop(1)
                .collectLatest { isConnected ->
                    if (isConnected) fetchAndUpdate()
                }
        }
    }

    private fun fetchAndUpdate() {
        viewModelScope.launch {
            val isConnected = networkObserver.isConnected.first()
            if (!isConnected) return@launch

            val current = itemFavourite.filterNotNull().first()
            val lat = current.lat
            val lon = current.lon
            val city = current.city
            try {
                val currentWeather = repository.getCurrentWeather(lat, lon)
                val hourly = repository.getHourlyForecast(city)
                val fiveDay = repository.getFiveDayForecast(city)

                if (currentWeather.isSuccessful && hourly.isSuccessful && fiveDay.isSuccessful) {
                    repository.insert(
                        current.copy(
                            currentWeather = currentWeather.body(),
                            hourlyForecast = hourly.body(),
                            fiveDayForecast = fiveDay.body()
                        )
                    )
                }
            } catch (e: Exception) {
            }
        }
    }
}
class DetailsViewModelFactory(
    private val repository: Repository,
    private val locationId: Int,
    private val networkObserver: CheckNetwork
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsFavoritesViewModel(repository, locationId, networkObserver) as T
    }
}
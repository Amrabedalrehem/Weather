package com.example.presentation.setting.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.presentation.utils.CheckNetwork
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsViewModel(private val repository: Repository,private val  networkObserver: CheckNetwork
, private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    val temperature = repository.temperatureUnit
    val windSpeed = repository.windSpeedUnit
    val language = repository.language
    val locationType = repository.locationType
    val theme = repository.theme
    val isConnected: StateFlow<Boolean> = networkObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    fun saveTemperature(value: String) = viewModelScope.launch {
        repository.saveTemperatureUnit(value)
    }
    fun saveWindSpeed(value: String) = viewModelScope.launch {
        repository.saveWindSpeedUnit(value)
    }
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        val cancellationToken = CancellationTokenSource()
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).await()

                if (location != null) {
                    repository.saveLocation(location.latitude, location.longitude)
                    repository.saveLocationType("gps")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cancellationToken.cancel()
            }
        }
    }

    suspend fun saveLanguageAndWait(value: String) {
        repository.saveLanguage(value)
    }
    fun saveLocationType(value: String) = viewModelScope.launch {
        repository.saveLocationType(value)
    }
    fun saveTheme(value: String) = viewModelScope.launch {
        repository.saveTheme(value)
    }
}

class SettingsViewModelFactory(private val repository: Repository, private val networkObserver: CheckNetwork, private val fusedLocationClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository, networkObserver, fusedLocationClient) as T
    }
}
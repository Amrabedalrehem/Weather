package com.example.presentation.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: Repository) : ViewModel() {

    val temperature = repository.temperatureUnit
    val windSpeed = repository.windSpeedUnit
    val language = repository.language
    val locationType = repository.locationType
    val theme = repository.theme

    fun saveTemperature(value: String) = viewModelScope.launch {
        repository.saveTemperatureUnit(value)
    }
    fun saveWindSpeed(value: String) = viewModelScope.launch {
        repository.saveWindSpeedUnit(value)
    }
    fun saveLanguage(value: String) = viewModelScope.launch {
        repository.saveLanguage(value)
    }
    fun saveLocationType(value: String) = viewModelScope.launch {
        repository.saveLocationType(value)
    }
    fun saveTheme(value: String) = viewModelScope.launch {
        repository.saveTheme(value)
    }
}

class SettingsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository) as T
    }
}
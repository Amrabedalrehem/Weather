package com.example.presentation.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: Repository) : ViewModel() {

    private val _navigateTo = MutableStateFlow("")
    val navigateTo: StateFlow<String> = _navigateTo

    fun checkPermission(hasPermission: Boolean) {
        viewModelScope.launch {
            val lat = repository.latitude.first()
            val lon = repository.longitude.first()

            if (hasPermission && lat != 0.0 && lon != 0.0) {
                _navigateTo.value = "home"
            } else {
                _navigateTo.value = "permission"
            }
        }
    }
}

class SplashViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashViewModel(repository) as T
    }
}
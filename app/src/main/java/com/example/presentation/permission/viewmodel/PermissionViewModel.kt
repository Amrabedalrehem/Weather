package com.example.presentation.permission.viewmodel
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.presentation.component.permission.LocationState
import com.example.presentation.component.permission.PermissionUiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<PermissionUiState>(PermissionUiState.Idle)
    val uiState: StateFlow<PermissionUiState> = _uiState

    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    fun onButtonClicked(
        hasPermission: Boolean,
        shouldShowRationale: Boolean
    ) {
        _uiState.value = when {
            hasPermission -> PermissionUiState.NavigateToHome

            !hasPermission && !shouldShowRationale && wasPermissionRequestedBefore() ->
                PermissionUiState.GoToSettings

            shouldShowRationale -> PermissionUiState.ShowRationale

            else -> PermissionUiState.RequestPermission
        }
    }

     fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        _uiState.value = when {
            isGranted -> {
                getCurrentLocation()
                PermissionUiState.NavigateToHome
            }
            shouldShowRationale -> PermissionUiState.ShowRationale
            else -> PermissionUiState.GoToSettings
        }
         if (!isGranted) markPermissionRequested()
    }

    fun resetState() {
        _uiState.value = PermissionUiState.Idle
    }

     private fun wasPermissionRequestedBefore(): Boolean {
        val prefs = getApplication<Application>()
            .getSharedPreferences("permission_prefs", android.content.Context.MODE_PRIVATE)
        return prefs.getBoolean("permission_requested", false)
    }

    private fun markPermissionRequested() {
        getApplication<Application>()
            .getSharedPreferences("permission_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putBoolean("permission_requested", true)
            .apply()
    }

     @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val cancellationToken = CancellationTokenSource()
        viewModelScope.launch {
            _locationState.value = _locationState.value.copy(isLoading = true)
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).await()

                _locationState.value = location?.let {
                    LocationState(latitude = it.latitude, longitude = it.longitude)
                } ?: LocationState(error = "Location not found")

            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            } finally {
                cancellationToken.cancel()
            }
        }
    }
}
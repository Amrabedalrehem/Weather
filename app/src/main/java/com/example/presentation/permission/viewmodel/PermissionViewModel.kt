package com.example.presentation.permission.viewmodel
import android.annotation.SuppressLint
import android.app.Application
 import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.Repository
import com.example.presentation.component.permission.LocationState
import com.example.presentation.component.permission.PermissionUiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PermissionViewModel(application: Application,
                          private val repository: IRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<PermissionUiState>(PermissionUiState.Idle)
    val uiState: StateFlow<PermissionUiState> = _uiState

    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)


    fun resetState() {
        _uiState.value = PermissionUiState.Idle
    }
    fun onButtonClicked(
        hasPermission: Boolean,
        shouldShowRationale: Boolean
    ) {
        viewModelScope.launch {
            val wasPermissionRequestedBefore = repository.wasPermissionRequested.first()

            if (hasPermission) {
                 getCurrentLocation()
            } else {
                _uiState.value = when {
                    !hasPermission && !shouldShowRationale && wasPermissionRequestedBefore ->
                        PermissionUiState.GoToSettings

                    shouldShowRationale -> PermissionUiState.ShowRationale

                    else -> PermissionUiState.RequestPermission
                }
            }
        }
    }

    fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {

        viewModelScope.launch {
            repository.markPermissionRequested()
            if (isGranted) {
                getCurrentLocation()
            } else {
                _uiState.value = when {
                    shouldShowRationale -> PermissionUiState.ShowRationale
                    else -> PermissionUiState.GoToSettings
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation() {
        val cancellationToken = CancellationTokenSource()
        _locationState.value = _locationState.value.copy(isLoading = true)
        try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()

            if (location != null) {

                repository.saveLocation(location.latitude, location.longitude)
                _locationState.value = LocationState(
                    latitude = location.latitude,
                    longitude = location.longitude
                     )

                _uiState.value = PermissionUiState.NavigateToHome
            } else {
                 _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    error = "Location is unavailable. Please enable GPS."
                )
                _uiState.value = PermissionUiState.ShowLocationError
            }

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
class PermissionViewModelFactory(
    private val application: Application,
    private val repository: IRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PermissionViewModel(application, repository) as T
    }
}
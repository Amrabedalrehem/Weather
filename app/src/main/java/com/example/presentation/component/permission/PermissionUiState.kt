package com.example.presentation.component.permission

sealed class PermissionUiState {
    data object Idle : PermissionUiState()
    data object RequestPermission : PermissionUiState()
    data object ShowRationale : PermissionUiState()
    data object GoToSettings : PermissionUiState()
    data object NavigateToHome : PermissionUiState()
}
package com.example.presentation.component.permission

data class LocationState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)
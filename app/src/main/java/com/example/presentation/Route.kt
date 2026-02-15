package com.example.presentation

import kotlinx.serialization.Serializable

@Serializable
sealed class RouteScreen {
    @Serializable
    data object Logo : RouteScreen()

}
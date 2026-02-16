package com.example.presentation

import kotlinx.serialization.Serializable

@Serializable
sealed class RouteScreen {

    @Serializable
    data object Logo : RouteScreen()

    @Serializable
    data object Permission : RouteScreen()


    @Serializable
    data object Settings : RouteScreen()

}
package com.example.presentation

import kotlinx.serialization.Serializable

@Serializable
sealed class RouteScreen {

    @Serializable
    data object Splash : RouteScreen()

    @Serializable
    data object Permission : RouteScreen()


    @Serializable
    data object Settings : RouteScreen()

    @Serializable
    data object Home : RouteScreen()

    @Serializable
    data object Favorite : RouteScreen()


    @Serializable
    data object Alarms : RouteScreen()


}
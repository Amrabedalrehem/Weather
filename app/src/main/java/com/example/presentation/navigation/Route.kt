package com.example.presentation.navigation

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

    @Serializable
    data object Map : RouteScreen()


    @Serializable
    data class DetailsFavorites(val id: Int) : RouteScreen()
}
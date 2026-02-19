package com.example.presentation.navigation

 import com.example.weather.R

data class NavigationItem(
    val title: String,
    val icon: Int,
    val route: RouteScreen
)

val navigationItems = listOf(
    NavigationItem(
        title = "Weather",
        icon = R.drawable.home_could,
        route = RouteScreen.Home
    ),
    NavigationItem(
        title = "Favorite",
        icon = R.drawable.nav_heart,
        route = RouteScreen.Favorite
    ),
    NavigationItem(
        title = "Alarms",
        icon = R.drawable.nav_alert,
        route = RouteScreen.Alarms
    ),
    NavigationItem(
        title = "Setting",
        icon = R.drawable.nav_settings,
        route = RouteScreen.Settings
    )
)
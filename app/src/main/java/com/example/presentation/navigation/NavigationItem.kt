package com.example.presentation.navigation

 import com.example.weather.R

data class NavigationItem(
    val titleResId: Int,
    val icon: Int,
    val route: RouteScreen
)

val navigationItems = listOf(
    NavigationItem(
        titleResId = R.string.nav_weather,
        icon = R.drawable.cloudynav,
        route = RouteScreen.Home
    ),
    NavigationItem(
        titleResId = R.string.nav_favorite,
        icon = R.drawable.nav_heart,
        route = RouteScreen.Favorite
    ),
    NavigationItem(
        titleResId = R.string.nav_alarms,
        icon = R.drawable.nav_alert,
        route = RouteScreen.Alarms
    ),
    NavigationItem(
        titleResId = R.string.nav_setting,
        icon = R.drawable.nav_settings,
        route = RouteScreen.Settings
    )
)
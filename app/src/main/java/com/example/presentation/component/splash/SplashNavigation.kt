package com.example.presentation.component.splash

sealed class SplashNavigation {
    object Home : SplashNavigation()
    object Permission : SplashNavigation()
}
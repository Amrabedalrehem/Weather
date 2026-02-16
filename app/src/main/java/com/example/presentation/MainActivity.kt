package com.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.favorite.view.FavoriteScreen
import com.example.presentation.home.view.HomeScreen
import com.example.presentation.splash.view.SplashScreen
import com.example.presentation.permission.view.PermissionScreen
import com.example.presentation.setting.view.SettingsScreen
import com.example.presentation.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                 val navController: NavHostController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = RouteScreen.Favorite
                    ) {
                        composable<RouteScreen.Splash> {
                            SplashScreen(
                                modifier = Modifier.padding(innerPadding),
                                onNavigateToHome = {
                                navController.navigate(RouteScreen.Permission)
                                }
                            )
                        }
                        composable<RouteScreen.Splash> {
                            PermissionScreen(
                                modifier = Modifier.padding(innerPadding),
                                onNavigateToHome = {
                                    //   navController.navigate(RouteScreen.Home)
                                }
                            )
                        }
                        composable<RouteScreen.Home> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),

                                )
                        }
                        composable<RouteScreen.Settings> {
                                SettingsScreen(
                                modifier = Modifier.padding(innerPadding),

                            )
                        }
                        composable<RouteScreen.Favorite> {
                            FavoriteScreen(
                                modifier = Modifier.padding(innerPadding),

                                )
                        }

                    }
                }


                }
            }
        }
    }



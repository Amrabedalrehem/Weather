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
import com.example.presentation.view.logo.LogoScreen
import com.example.presentation.view.theme.WeatherTheme

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
                        startDestination = RouteScreen.Logo
                    ) {
                        composable<RouteScreen.Logo> {
                            LogoScreen(
                                modifier = Modifier.padding(innerPadding),
                                onNavigateToHome = {
                                 //   navController.navigate(RouteScreen.Home)
                                }
                            )
                        }
                    }
                }


                }
            }
        }
    }



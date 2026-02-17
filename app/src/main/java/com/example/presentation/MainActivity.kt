package com.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.Repository
import com.example.data.datasource.local.DataSourceLocal
import com.example.data.datasource.remote.DataSourceRemote
import com.example.presentation.alarms.view.AlarmsScreen
import com.example.presentation.favorite.view.FavoriteScreen
import com.example.presentation.home.view.HomeScreen
import com.example.presentation.home.viewmodel.HomeViewModel
import com.example.presentation.home.viewmodel.HomeViewModelFactory
import com.example.presentation.splash.view.SplashScreen
import com.example.presentation.permission.view.PermissionScreen
import com.example.presentation.setting.view.SettingsScreen
import com.example.presentation.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    private val dataSourceRemote = DataSourceRemote()
    private val dataSourceLocal = DataSourceLocal()
    private val repository = Repository(dataSourceLocal, dataSourceRemote)

    private val factory = HomeViewModelFactory(repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: HomeViewModel  = viewModel(factory =factory)
            WeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                 val navController: NavHostController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = RouteScreen.Home
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
                                viewModel = viewModel,
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

                        composable<RouteScreen.Alarms> {
                            AlarmsScreen(
                                modifier = Modifier.padding(innerPadding),

                                )
                        }

                    }
                }


                }
            }
        }
    }



package com.example.presentation

import com.example.weather.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.data.Repository
import com.example.data.datasource.local.DataSourceLocal
import com.example.data.datasource.remote.DataSourceRemote
import com.example.presentation.alarms.view.AlarmsScreen
import com.example.presentation.favorite.view.FavoriteScreen
import com.example.presentation.home.view.HomeScreen
import com.example.presentation.home.viewmodel.HomeViewModel
import com.example.presentation.home.viewmodel.HomeViewModelFactory
import com.example.presentation.permission.view.PermissionScreen
import com.example.presentation.setting.view.SettingsScreen
import com.example.presentation.splash.view.SplashScreen
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
            val navController: NavHostController = rememberNavController()
            val viewModel: HomeViewModel = viewModel(factory = factory)

            WeatherTheme {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf(
                    RouteScreen.Home::class.qualifiedName,
                    RouteScreen.Favorite::class.qualifiedName,
                    RouteScreen.Alarms::class.qualifiedName,
                    RouteScreen.Settings::class.qualifiedName
                )

                val showFavoriteAB = currentRoute == RouteScreen.Favorite::class.qualifiedName
                val showAlarmsAB = currentRoute == RouteScreen.Alarms::class.qualifiedName

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) BottomNavigationBar(navController)
                    },
                    floatingActionButton = {
                        if (showFavoriteAB) {
                            FloatingActionButton(
                                onClick = { },
                                shape = RoundedCornerShape(30.dp),
                                containerColor = Color.White.copy(0.8f)
                            ) {
                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.RawRes(R.raw.quick)
                                )
                                LottieAnimation(
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        if (showAlarmsAB) {
                            FloatingActionButton(
                                onClick = { },
                                shape = RoundedCornerShape(30.dp),
                                containerColor = Color.White.copy(0.8f)
                            ) {
                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.RawRes(R.raw.notificationbell)
                                )
                                LottieAnimation(
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                        }
                    }
                ) { innerPadding ->


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
                        composable<RouteScreen.Permission> {
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


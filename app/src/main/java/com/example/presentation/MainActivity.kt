package com.example.presentation
import com.example.weather.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.datasource.sharedPreference.DataStorePermission
import com.example.data.datasource.sharedPreference.DataStoreSettings
import com.example.data.dp.AppDatabase
import com.example.presentation.alarms.view.AlarmsScreen
import com.example.presentation.component.location.MapPickerScreen
import com.example.presentation.component.location.MapPickerViewModel
import com.example.presentation.component.location.MapPickerViewModelFactory
import com.example.presentation.favorite.view.FavoriteScreen
import com.example.presentation.favorite.viewmodel.FavoritesViewModel
import com.example.presentation.favorite.viewmodel.FavoritesViewModelFactory
import com.example.presentation.home.view.HomeScreen
import com.example.presentation.home.viewmodel.HomeViewModel
import com.example.presentation.home.viewmodel.HomeViewModelFactory
import com.example.presentation.navigation.BottomNavigationBar
import com.example.presentation.navigation.RouteScreen
import com.example.presentation.permission.view.PermissionScreen
import com.example.presentation.permission.viewmodel.PermissionViewModel
import com.example.presentation.permission.viewmodel.PermissionViewModelFactory
import com.example.presentation.setting.view.SettingsScreen
import com.example.presentation.setting.viewmodel.SettingsViewModel
import com.example.presentation.setting.viewmodel.SettingsViewModelFactory
import com.example.presentation.splash.view.SplashScreen
import com.example.presentation.splash.viewmodel.SplashViewModel
import com.example.presentation.splash.viewmodel.SplashViewModelFactory
import com.example.presentation.theme.WeatherTheme
import com.example.weather.BuildConfig
 import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    private val dataSourceRemote = DataSourceRemote()
    private val database by lazy { AppDatabase.getInstance(this) }
    private val dataSourceLocal by lazy { DataSourceLocal(database.favouriteDao()) }
    private val dataStoreSettings by lazy { DataStoreSettings(this) }
    private val dataStorePermission by lazy { DataStorePermission(this) }
    private val repository by lazy {
        Repository(dataSourceLocal, dataSourceRemote, dataStoreSettings, dataStorePermission)
    }
    private val factory by lazy { HomeViewModelFactory(repository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
        enableEdgeToEdge()
        setContent {
            val appScope = rememberCoroutineScope()
            val navController: NavHostController = rememberNavController()
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
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
                 val snackbarHostState = remember { SnackbarHostState() }
                 Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                    snackbarData = data,
                                    containerColor = Color(0xFF2E4A6B),
                                    contentColor = Color.White,
                                )
                            }
                        },
                        bottomBar = {
                            if (showBottomBar) BottomNavigationBar(navController)
                        },
                        floatingActionButton = {
                            if (showFavoriteAB) {
                                FloatingActionButton(
                                    onClick = {
                                        navController.navigate(RouteScreen.Map)
                                    },
                                    shape = CircleShape,
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
                                    shape = CircleShape,
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
                            startDestination = RouteScreen.Splash
                        ) {
                            composable<RouteScreen.Splash> {
                                val splashViewModel: SplashViewModel = viewModel(
                                    factory = SplashViewModelFactory(repository)
                                )
                                SplashScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    viewModel = splashViewModel,
                                    onNavigateToHome = {
                                        navController.navigate(RouteScreen.Home) {
                                            popUpTo(RouteScreen.Splash) { inclusive = true }
                                        }
                                    },
                                    onNavigateToPermission = {
                                        navController.navigate(RouteScreen.Permission) {
                                            popUpTo(RouteScreen.Splash) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable<RouteScreen.Map> {
                                val mapViewModel: MapPickerViewModel = viewModel(
                                    factory = MapPickerViewModelFactory(repository)
                                )

                                if (mapViewModel.isLocationLoaded) {
                                    MapPickerScreen(
                                        onLocationSelected = { _, _ ->
                                            navController.popBackStack()

                                        },

                                        initialLocation = mapViewModel.defaultLocation,
                                        nav = navController,
                                        showInitialMarker = true,
                                        viewModel = mapViewModel,
                                        snackbarHostState = snackbarHostState,
                                        appScope = appScope


                                        )
                                }
                            }
                            composable<RouteScreen.Permission> {
                                val permissionViewModel: PermissionViewModel = viewModel(
                                    factory = PermissionViewModelFactory(application, repository)
                                )
                                PermissionScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    viewModel = permissionViewModel,
                                    onNavigateToHome = {
                                        navController.navigate(RouteScreen.Home) {
                                            popUpTo(RouteScreen.Permission) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable<RouteScreen.Home> {
                                HomeScreen(
                                    viewModel = homeViewModel,
                                    modifier = Modifier.padding(innerPadding),

                                    )
                            }
                            composable<RouteScreen.Settings> {
                                val settingsViewModel: SettingsViewModel = viewModel(
                                    factory = SettingsViewModelFactory(repository)
                                )
                                SettingsScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    viewModel = settingsViewModel,
                                    snackbarHostState = snackbarHostState,
                                    onNavigateToMap = {
                                        navController.navigate(RouteScreen.Map)
                                    }
                                )
                            }
                            composable<RouteScreen.Favorite> {
                                val favoriteViewModel: FavoritesViewModel = viewModel(
                                    factory = FavoritesViewModelFactory(repository)
                                )
                                FavoriteScreen(
                                    snackbarHostState = snackbarHostState,
                                    modifier = Modifier.padding(innerPadding),
                                   viewModel = favoriteViewModel,
                                    onFavouriteClick = {
                                        navController.navigate(RouteScreen.Map)
                                    }
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


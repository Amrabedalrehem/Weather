package com.example.presentation
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.work.WorkManager
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.data.Repository
import com.example.presentation.component.alert.alarm.WeatherWorkerFactory
import com.example.data.datasource.local.DataSourceLocal
import com.example.data.datasource.remote.DataSourceRemote
import com.example.data.datasource.sharedPreference.DataStorePermission
import com.example.data.datasource.sharedPreference.DataStoreSettings
import com.example.data.dp.AppDatabase
import com.example.presentation.utils.CheckNetwork
import com.example.presentation.alarms.view.AlarmsScreen
import com.example.presentation.alarms.viewmodel.AlarmViewModel
import com.example.presentation.alarms.viewmodel.AlarmViewModelFactory
import com.example.presentation.component.location.MapPickerScreen
import com.example.presentation.component.location.MapPickerViewModel
import com.example.presentation.component.location.MapPickerViewModelFactory
import com.example.presentation.detailsfavourites.view.DetailsFavoritesScreen
import com.example.presentation.detailsfavourites.viewmodel.DetailsFavoritesViewModel
import com.example.presentation.detailsfavourites.viewmodel.DetailsViewModelFactory
import com.example.presentation.favorite.view.FavoriteScreen
import com.example.presentation.favorite.viewmodel.FavoritesViewModel
import com.example.presentation.favorite.viewmodel.FavoritesViewModelFactory
import com.example.presentation.futureinfo.view.FutureInfoScreen
import com.example.presentation.futureinfo.viewmodel.FutureInfoViewModel
import com.example.presentation.futureinfo.viewmodel.FutureInfoViewModelFactory
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
import android.content.res.Configuration
import android.os.Build
import android.app.LocaleManager
import android.os.LocaleList
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val dataSourceRemote = DataSourceRemote()
    private val database by lazy { AppDatabase.getInstance(this) }
    private val dataSourceLocal by lazy {
        DataSourceLocal(
            database.favouriteDao(),
            database.homeWeatherDao(),
            database.alarmDao()
        )
    }
    private val dataStoreSettings by lazy { DataStoreSettings(this) }
    private val dataStorePermission by lazy { DataStorePermission(this) }
    private val networkObserver by lazy { CheckNetwork(this) }
    private val repository by lazy {
        Repository(dataSourceLocal, dataSourceRemote, dataStoreSettings, dataStorePermission)
    }
    private val workerFactory by lazy { WeatherWorkerFactory(repository) }
    private val factory by lazy { HomeViewModelFactory(repository, networkObserver) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
        enableEdgeToEdge()

        // Restore saved locale on app startup
        val savedLang = runBlocking { repository.language.first() }
        val localeTag = when (savedLang) {
            "العربية" -> "ar"
            "English" -> "en"
            else -> ""
        }
        if (localeTag.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val localeManager = getSystemService(LocaleManager::class.java)
                localeManager.applicationLocales = LocaleList.forLanguageTags(localeTag)
            } else {
                val locale = Locale(localeTag)
                Locale.setDefault(locale)
                val config = Configuration(resources.configuration)
                config.setLocale(locale)
                config.setLayoutDirection(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }
        if (!WorkManager.isInitialized()) {
            WorkManager.initialize(
                this,
                androidx.work.Configuration.Builder()
                    .setWorkerFactory(workerFactory)
                    .build()
            )
        }
        setContent {
            val savedLanguage by repository.language.collectAsState(initial = "English")
            val layoutDirection = if (savedLanguage == "العربية") LayoutDirection.Rtl else LayoutDirection.Ltr

            val appScope = rememberCoroutineScope()
            val navController: NavHostController = rememberNavController()
            val homeViewModel: HomeViewModel = viewModel(factory = factory)

            WeatherTheme {
                 CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {

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
                    var openAlarmSheet: (() -> Unit)? by remember { mutableStateOf(null) }

                    Scaffold(
                        containerColor = Color.Transparent,
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                    snackbarData = data,
                                    containerColor = Color(0xFF1976D2),
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
                                    onClick = { navController.navigate(RouteScreen.FutureInfo) },
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
                        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
                                        factory = MapPickerViewModelFactory(repository, networkObserver)
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
                                        factory = SettingsViewModelFactory(repository, networkObserver)
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
                                        factory = FavoritesViewModelFactory(repository, applicationContext)
                                    )
                                    FavoriteScreen(
                                        snackbarHostState = snackbarHostState,
                                        modifier = Modifier.padding(innerPadding),
                                        viewModel = favoriteViewModel,
                                        onFavouriteClick = { location ->
                                            navController.navigate(RouteScreen.DetailsFavorites(id = location.id))
                                        }
                                    )
                                }
                                composable<RouteScreen.Alarms> {
                                    val alarmViewModel: AlarmViewModel = viewModel(
                                        factory = AlarmViewModelFactory(repository, applicationContext)
                                    )
                                    AlarmsScreen(
                                        modifier = Modifier.padding(innerPadding),
                                        viewModel = alarmViewModel,
                                        onRequestAddAlarm = { callback ->
                                            openAlarmSheet = callback
                                        }
                                    )
                                }
                                composable<RouteScreen.DetailsFavorites> { backStackEntry ->
                                    val detailsRoute = backStackEntry.toRoute<RouteScreen.DetailsFavorites>()
                                    val locationId = detailsRoute.id
                                    val detailsViewModel: DetailsFavoritesViewModel = viewModel(
                                        factory = DetailsViewModelFactory(
                                            repository,
                                            locationId,
                                            networkObserver
                                        )
                                    )
                                    DetailsFavoritesScreen(
                                        locationId = locationId,
                                        viewModel = detailsViewModel,
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                                composable<RouteScreen.FutureInfo> {
                                    val futureInfoViewModel: FutureInfoViewModel = viewModel(
                                        factory = FutureInfoViewModelFactory(application, repository)
                                    )
                                    FutureInfoScreen(
                                        viewModel = futureInfoViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }

                            var showBanner by remember { mutableStateOf(true) }
                            val isConnected by networkObserver.isConnected.collectAsState(initial = true)
                            LaunchedEffect(isConnected) {
                                if (!isConnected) showBanner = true
                            }

                            AnimatedVisibility(
                                visible = !isConnected && showBanner,
                                enter = slideInVertically { -it },
                                exit = slideOutVertically { -it },
                                modifier = Modifier.align(Alignment.TopCenter)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Red.copy(alpha = 0.8f))
                                        .padding(8.dp)
                                        .clickable { showBanner = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_internet_banner),
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
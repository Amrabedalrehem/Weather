package com.example.presentation.setting.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.presentation.component.setting.SettingsCard
import com.example.presentation.setting.viewmodel.SettingsViewModel
import com.example.presentation.utils.LocaleHelper
import com.example.weather.R
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToMap: () -> Unit,
) {
    val temperatureKey by viewModel.temperature.collectAsState(initial = "celsius")
    val windSpeedKey   by viewModel.windSpeed.collectAsState(initial = "ms")
    val languageKey    by viewModel.language.collectAsState(initial = "default")
    val locationKey    by viewModel.locationType.collectAsState(initial = "gps")
    val themeKey       by viewModel.theme.collectAsState(initial = "system")
    var showMapDialog  by remember { mutableStateOf(false) }
    val isConnected    by viewModel.isConnected.collectAsState()
    val scope          = rememberCoroutineScope()
    val context        = LocalContext.current

    val noInternetMsg       = stringResource(R.string.no_internet)
    val optionSelectedPattern = stringResource(R.string.option_selected)

    val langOptions = listOf(
        "default" to stringResource(R.string.lang_default),
        "en"      to stringResource(R.string.lang_english),
        "ar"      to stringResource(R.string.lang_arabic)
    )
    val tempOptions = listOf(
        "celsius"    to stringResource(R.string.temp_celsius),
        "kelvin"     to stringResource(R.string.temp_kelvin),
        "fahrenheit" to stringResource(R.string.temp_fahrenheit)
    )
    val locOptions = listOf(
        "gps" to stringResource(R.string.loc_gps),
        "map" to stringResource(R.string.loc_map)
    )
    val windOptions = listOf(
        "ms"  to stringResource(R.string.wind_ms),
        "mph" to stringResource(R.string.wind_mph)
    )
    val themeOptions = listOf(
        "system" to stringResource(R.string.theme_system),
        "dark"   to stringResource(R.string.theme_dark),
        "light"  to stringResource(R.string.theme_light)
    )

    fun List<Pair<String, String>>.labelFor(key: String) =
        firstOrNull { it.first == key }?.second ?: first().second

    fun List<Pair<String, String>>.keyFor(label: String) =
        firstOrNull { it.second == label }?.first ?: first().first

    val showSnackbar: (String) -> Unit = { selectedLabel ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            if (!isConnected) {
                snackbarHostState.showSnackbar(noInternetMsg)
            } else {
                snackbarHostState.showSnackbar(optionSelectedPattern.replace("%1\$s", selectedLabel))
            }
        }
    }
    if (showMapDialog) {
        AlertDialog(
            onDismissRequest = { showMapDialog = false },
            title = {
                Text(text = stringResource(R.string.change_location_title))
            },
            text = {
                Text(text = stringResource(R.string.change_location_message))
            },
            confirmButton = {
                TextButton(onClick = {
                    showMapDialog = false
                    viewModel.saveLocationType("map")
                    showSnackbar(locOptions.labelFor("map"))
                    onNavigateToMap()
                }) {
                    Text(stringResource(R.string.yes), color = Color(0xFF3B82F6))
                }
            },
            dismissButton = {
                TextButton(onClick = { showMapDialog = false }) {
                    Text(stringResource(R.string.no), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }
        )
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = com.example.presentation.theme.LocalWeatherGradient.current
                )
            )
            .padding(vertical = 12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top    = 0.dp,
            bottom = 80.dp
        )
    ) {
        item {
            SettingsCard(
                title          = stringResource(R.string.language),
                icon           = R.drawable.languages,
                options        = langOptions.map { it.second },
                selectedOption = langOptions.labelFor(languageKey),
                onOptionSelected = { label ->
                    val key       = langOptions.keyFor(label)
                    val localeTag = when (key) {
                        "en" -> "en"
                        "ar" -> "ar"
                        else -> ""
                    }
                    scope.launch {
                        viewModel.saveLanguageAndWait(key)
                        LocaleHelper.applyLocale(context, localeTag)
                    }
                }
            )
        }
        item {
            SettingsCard(
                title          = stringResource(R.string.temperature_unit),
                icon           = R.drawable.temperature,
                options        = tempOptions.map { it.second },
                selectedOption = tempOptions.labelFor(temperatureKey),
                onOptionSelected = { label ->
                    val key = tempOptions.keyFor(label)
                    viewModel.saveTemperature(key)
                    showSnackbar(label)
                }
            )
        }
        item {
            SettingsCard(
                title          = stringResource(R.string.location),
                icon           = R.drawable.map,
                options        = locOptions.map { it.second },
                selectedOption = locOptions.labelFor(locationKey),
                onOptionSelected = { label ->
                    val key = locOptions.keyFor(label)
                    if (key == "map") {
                        showMapDialog = true
                    } else {
                        viewModel.getCurrentLocation()
                        showSnackbar(label)
                    }
                }
            )
        }
        item {
            SettingsCard(
                title          = stringResource(R.string.wind_speed_unit),
                icon           = R.drawable.wind,
                options        = windOptions.map { it.second },
                selectedOption = windOptions.labelFor(windSpeedKey),
                onOptionSelected = { label ->
                    val key = windOptions.keyFor(label)
                    viewModel.saveWindSpeed(key)
                    showSnackbar(label)
                }
            )
        }
        item {
            SettingsCard(
                title          = stringResource(R.string.theme),
                icon           = R.drawable.color,
                options        = themeOptions.map { it.second },
                selectedOption = themeOptions.labelFor(themeKey),
                onOptionSelected = { label ->
                    val key = themeOptions.keyFor(label)
                    viewModel.saveTheme(key)
                    showSnackbar(label)
                }
            )
        }
    }
}
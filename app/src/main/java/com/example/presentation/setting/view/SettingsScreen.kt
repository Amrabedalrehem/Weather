package com.example.presentation.setting.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
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
    val temperature by viewModel.temperature.collectAsState(initial = "Celsius (°C)")
    val windSpeed by viewModel.windSpeed.collectAsState(initial = "m/s")
    val language by viewModel.language.collectAsState(initial = "English")
    val locationType by viewModel.locationType.collectAsState(initial = "Gps")
    val theme by viewModel.theme.collectAsState(initial = "System")
    var showMapDialog by remember { mutableStateOf(false) }
    val isConnected by viewModel.isConnected.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val noInternetMsg = stringResource(R.string.no_internet)
    val optionSelectedPattern = stringResource(R.string.option_selected)

    val langEnglishLabel = stringResource(R.string.lang_english)
    val langArabicLabel = stringResource(R.string.lang_arabic)
    val locMapLabel = stringResource(R.string.loc_map)

    val showSnackbar: (String) -> Unit = { selectedOption ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            if (!isConnected) {
                snackbarHostState.showSnackbar(noInternetMsg)
            } else {
                snackbarHostState.showSnackbar(optionSelectedPattern.replace("%1\$s", selectedOption))
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
                    viewModel.saveLocationType("Map")
                    showSnackbar("Map")
                    onNavigateToMap()
                }) {
                    Text(stringResource(R.string.yes), color = Color(0xFF3B82F6))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showMapDialog = false
                }) {
                    Text(stringResource(R.string.no), color = Color.Gray)
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
                    colors = listOf(
                        Color(0xFF2196F3),
                        Color(0xFF03A9F4),
                        Color(0xFF00BCD4)
                    )
                )
            ).padding(vertical = 12.dp),
         contentPadding = androidx.compose.foundation.layout.PaddingValues(
             top = 0.dp,
             bottom = 80.dp
         )
    ) {
        item {
            SettingsCard(
                title = stringResource(R.string.language),
                icon = R.drawable.languages,
                options = listOf(
                    stringResource(R.string.lang_default),
                    langEnglishLabel,
                    langArabicLabel
                ),
                selectedOption = language,
                onOptionSelected = {
                    val selectedLang = it
                    val localeTag = when (selectedLang) {
                        "English", langEnglishLabel -> "en"
                        "العربية", langArabicLabel -> "ar"
                        else -> ""
                    }
                    scope.launch {
                        viewModel.saveLanguageAndWait(selectedLang)
                        LocaleHelper.applyLocale(context, localeTag)
                    }
                }
            )
        }
        item {
            SettingsCard(
                title = stringResource(R.string.temperature_unit),
                icon = R.drawable.temperature,
                options = listOf(
                    stringResource(R.string.temp_celsius),
                    stringResource(R.string.temp_kelvin),
                    stringResource(R.string.temp_fahrenheit)
                ),
                selectedOption = temperature,
                onOptionSelected = {
                    viewModel.saveTemperature(it)
                    showSnackbar(it)
                }
            )
        }
        item {
            SettingsCard(
                title = stringResource(R.string.location),
                icon = R.drawable.map,
                options = listOf(
                    stringResource(R.string.loc_gps),
                    locMapLabel
                ),
                selectedOption = locationType,
                onOptionSelected = {
                    if (it == "Map" || it == locMapLabel) {
                        showMapDialog = true
                    } else {
                        viewModel.saveLocationType(it)
                        showSnackbar(it)
                    }
                }
            )
        }
        item {
            SettingsCard(
                title = stringResource(R.string.wind_speed_unit),
                icon = R.drawable.wind,
                options = listOf(
                    stringResource(R.string.wind_ms),
                    stringResource(R.string.wind_mph)
                ),
                selectedOption = windSpeed,
                onOptionSelected = {
                    viewModel.saveWindSpeed(it)
                    showSnackbar(it)
                }
            )
        }
        item {
            SettingsCard(
                title = stringResource(R.string.theme),
                icon = R.drawable.color,
                options = listOf(
                    stringResource(R.string.theme_system),
                    stringResource(R.string.theme_dark),
                    stringResource(R.string.theme_light)
                ),
                selectedOption = theme,
                onOptionSelected = {
                    viewModel.saveTheme(it)
                    showSnackbar(it)
                }
            )
        }
    }
}

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
import androidx.compose.ui.unit.dp
import com.example.presentation.setting.viewmodel.SettingsViewModel
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


    val showSnackbar: (String) -> Unit = { selectedOption ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            if (!isConnected) {
                snackbarHostState.showSnackbar("No Internet Connection check your connection")
            } else {
                snackbarHostState.showSnackbar("✔ \"$selectedOption\" selected")
            }        }
    }
    if (showMapDialog) {
        AlertDialog(
            onDismissRequest = { showMapDialog = false },
            title = {
                Text(text = "Change Location?")
            },
            text = {
                Text(text = "Do you want to change your location using the map?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showMapDialog = false
                    viewModel.saveLocationType("Map")
                    showSnackbar("Map")
                    onNavigateToMap()
                }) {
                    Text("Yes", color = Color(0xFF3B82F6))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showMapDialog = false
                }) {
                    Text("No", color = Color.Gray)
                }
            }
        )
    }
     LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B8CB5),
                        Color(0xFF8BA5C9),
                        Color(0xFF9FB5D1)
                    )
                )
            )
            .padding(vertical = 12.dp)
    ) {
        item {
            SettingsCard(
                title = "Language",
                icon = R.drawable.languages,
                options = listOf("Default", "English", "العربية"),
                selectedOption = language,
                onOptionSelected = {
                    viewModel.saveLanguage(it)
                    showSnackbar(it)
                }
            )
        }
        item {
            SettingsCard(
                title = "Temperature Unit",
                icon = R.drawable.temperature,
                options = listOf("Celsius (°C)", "kelvin (°K)", "Fahrenheit (°F)"),
                selectedOption = temperature,
                onOptionSelected = {
                    viewModel.saveTemperature(it)
                    showSnackbar(it)
                }
            )
        }
        item {
            SettingsCard(
                title = "Location",
                icon = R.drawable.map,
                options = listOf("Gps", "Map"),
                selectedOption = locationType,
                onOptionSelected = {
                    if (it == "Map") {
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
                title = "Wind Speed Unit",
                icon = R.drawable.wind,
                options = listOf("m/s", "mph"),
                selectedOption = windSpeed,
                onOptionSelected = {
                    viewModel.saveWindSpeed(it)
                    showSnackbar(it)
                }
            )
        }
        item {
            SettingsCard(
                title = "Theme",
                icon = R.drawable.color,
                options = listOf("System", "Dark", "Light"),
                selectedOption = theme,
                onOptionSelected = {
                    viewModel.saveTheme(it)
                    showSnackbar(it)
                }
            )
        }
    }
}

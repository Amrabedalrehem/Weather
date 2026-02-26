package com.example.presentation.permission.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.utils.MyLottieAnimation
import com.example.presentation.component.permission.PermissionUiState
import com.example.presentation.permission.viewmodel.PermissionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    viewModel: PermissionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

     lateinit var locationPermissionState: MultiplePermissionsState
    locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) { permissions ->
        val isGranted = permissions.values.all { it }
         viewModel.onPermissionResult(
            isGranted = isGranted,
            shouldShowRationale = locationPermissionState.shouldShowRationale
        )
    }

    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            viewModel.onButtonClicked(
                hasPermission = true,
                shouldShowRationale = false
            )
        }
    }
    LaunchedEffect(uiState) {
        when (uiState) {
            is PermissionUiState.NavigateToHome -> {
                onNavigateToHome()
                viewModel.resetState()
            }
            is PermissionUiState.RequestPermission -> {
                locationPermissionState.launchMultiplePermissionRequest()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

if (uiState is PermissionUiState.ShowRationale) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text(stringResource(R.string.location_needed), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.location_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetState()
                    locationPermissionState.launchMultiplePermissionRequest()
                }) {
                    Text(stringResource(R.string.allow), color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetState() }) {
                    Text(stringResource(R.string.cancel), color = Color.Gray)
                }
            }
        )
    }

    if (uiState is PermissionUiState.ShowLocationError) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text(stringResource(R.string.gps_disabled), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.gps_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetState()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text(stringResource(R.string.enable_gps), color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetState() }) {
                    Text(stringResource(R.string.cancel), color = Color.Gray)
                }
            }
        )
    }

    if (uiState is PermissionUiState.GoToSettings) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text(stringResource(R.string.permission_denied), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.permission_denied_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetState()
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) {
                    Text(stringResource(R.string.go_to_settings), color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetState() }) {
                    Text(stringResource(R.string.cancel), color = Color.Gray)
                }
            }
        )
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = com.example.presentation.theme.LocalWeatherGradient.current
                )
            )
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(1000)) + scaleIn(tween(1000))
        ) {
            MyLottieAnimation(size = 200.dp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(1000, 500)) + slideInVertically(
                tween(1000, 500),
                initialOffsetY = { it / 2 }
            )
        ) {
            Text(
                text = stringResource(R.string.weather_title),
                fontSize = 40.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = isVisible, enter = fadeIn(tween(1000, 700))) {
            Text(
                text = stringResource(R.string.track_weather),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(1000, 1100)) + scaleIn(tween(1000, 1100))
        ) {
            Button(
                onClick = {
                     viewModel.onButtonClicked(
                        hasPermission = locationPermissionState.allPermissionsGranted,
                        shouldShowRationale = locationPermissionState.shouldShowRationale
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(21.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = when {
                        locationPermissionState.allPermissionsGranted -> stringResource(R.string.get_started)
                        locationPermissionState.shouldShowRationale -> stringResource(R.string.why_need_location)
                        else -> stringResource(R.string.allow_location)
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
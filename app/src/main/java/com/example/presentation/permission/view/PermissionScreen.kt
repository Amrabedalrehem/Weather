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
import com.example.presentation.component.helper.MyLottieAnimation
import com.example.presentation.component.permission.PermissionUiState
import com.example.presentation.permission.viewmodel.PermissionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        onPermissionsResult = { permissions ->
            val isGranted = permissions.values.all { it }

            viewModel.onPermissionResult(
                isGranted = isGranted,
                shouldShowRationale = permissions.keys.any { permission ->
                    !permissions[permission]!!
                }
            )
        }
    )

    val shouldShowRationale = locationPermissionState.shouldShowRationale

    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
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
            title = { Text("Location Needed") },
            text = { Text("We need your location to show accurate weather for your area. Please allow it.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetState()
                    locationPermissionState.launchMultiplePermissionRequest()
                }) {
                    Text("Allow", color = Color(0xFF4A6A8F))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetState() }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
    if (uiState is PermissionUiState.ShowLocationError) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text("GPS Disabled") },
            text = { Text("Please turn on your GPS to get accurate weather data.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetState()
                     val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text("Enable GPS", color = Color(0xFF4A6A8F))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetState() }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
    if (uiState is PermissionUiState.GoToSettings) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text("Permission Denied") },
            text = { Text("You've denied location permission. Please enable it from Settings to use the app.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetState()
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) {
                    Text("Go to Settings", color = Color(0xFF4A6A8F))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetState() }) {
                    Text("Cancel", color = Color.Gray)
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
                    colors = listOf(
                        Color(0xFF6B8CB5),
                        Color(0xFF8BA5C9),
                        Color(0xFF9FB5D1)
                    )
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
                text = "Weather",
                fontSize = 40.sp,
                color = Color(0xFF4A6A8F),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(1000, 700))
        ) {
            Text(
                text = "Track the weather anywhere.",
                fontSize = 16.sp,
                color = Color(0xFF5A7A9F),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(1000, 900))
        ) {
            Text(
                text = "Real-time forecasts, interactive maps,\nand smart alerts—all in one place.",
                fontSize = 14.sp,
                color = Color(0xFFE0E8F0),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
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
                    contentColor = Color(0xFF4A6A8F)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = when {
                        locationPermissionState.allPermissionsGranted -> "Get Started →"
                        locationPermissionState.shouldShowRationale -> "Why We Need Location"
                        else -> "Allow Location"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
package com.example.presentation.component.location

import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import com.example.presentation.theme.LocalWeatherGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    onLocationSelected: (LatLng, String) -> Unit,
    initialLocation: LatLng = LatLng(30.0444, 31.2357),
    nav: NavController,
    showInitialMarker: Boolean = false,
    viewModel: MapPickerViewModel,
    snackbarHostState: SnackbarHostState,
    appScope: CoroutineScope
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val selectedLocation = viewModel.selectedLocation
    val selectedAddress = viewModel.selectedAddress
    val selectedCity = viewModel.selectedCity
    val selectedCountry = viewModel.selectedCountry
    val windUnit by viewModel.windSpeedUnit.collectAsState()
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Address>>(emptyList()) }
    var showResults by remember { mutableStateOf(false) }
    val isConnected by viewModel.isConnected.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                viewModel.onMapClick(latLng, context)
                showBottomSheet = true
                scope.launch { sheetState.show() }
            }
        ) {
            val markerPosition = selectedLocation ?: if (showInitialMarker) initialLocation else null
            markerPosition?.let {
                Marker(
                    state = MarkerState(position = it),
                    anchor = Offset(0.5f, 1.0f)
                )
            }
        }

        if (selectedLocation != null) {
            val cameraPos = cameraPositionState.position
            val projection = cameraPositionState.projection
            val markerScreenPos = remember(cameraPos, selectedLocation) {
                projection?.toScreenLocation(selectedLocation!!)
            }

            markerScreenPos?.let { screenPoint ->
                var cardWidth by remember { mutableStateOf(0) }
                var cardHeight by remember { mutableStateOf(0) }

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = screenPoint.x - (cardWidth / 2),
                                y = screenPoint.y - cardHeight - 120
                            )
                        }
                        .onGloballyPositioned { coords ->
                            cardWidth = coords.size.width
                            cardHeight = coords.size.height
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = selectedCity.ifEmpty { selectedAddress.take(20) },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            if (selectedCountry.isNotEmpty()) {
                                Text(
                                    text = selectedCountry,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showSearchBar) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(start = 70.dp, end = 70.dp, top = 48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            if (query.length > 2) {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val geocoder = Geocoder(context, Locale.getDefault())
                                        val results = geocoder.getFromLocationName(query, 5)
                                        withContext(Dispatchers.Main) {
                                            searchResults = results ?: emptyList()
                                            showResults = true
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) { searchResults = emptyList() }
                                    }
                                }
                            } else {
                                showResults = false
                            }
                        },
                        placeholder = { Text(stringResource(R.string.search_for_city)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = ""; showResults = false }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear))
                        }
                    }
                }
                if (showResults && searchResults.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column {
                            searchResults.forEach { address ->
                                val displayName = address.locality
                                    ?: address.adminArea
                                    ?: address.countryName
                                    ?: stringResource(R.string.unknown)
                                Text(
                                    text = displayName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val latLng = LatLng(address.latitude, address.longitude)
                                            viewModel.onMapClick(latLng, context)
                                            searchQuery = displayName
                                            showResults = false
                                            showSearchBar = false
                                            showBottomSheet = true
                                            scope.launch {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngZoom(latLng, 12f)
                                                )
                                                sheetState.show()
                                            }
                                        }
                                        .padding(12.dp),
                                    fontSize = 14.sp
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                showSearchBar = !showSearchBar
                if (!showSearchBar) {
                    searchQuery = ""
                    showResults = false
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp),
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                contentDescription = "Search"
            )
        }
        FloatingActionButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp),
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        if (!isConnected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) {}
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Red.copy(alpha = 0.8f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_internet_banner), color = Color.White, fontSize = 14.sp)
            }

            FloatingActionButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 48.dp, start = 16.dp),
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    }

    if (showBottomSheet) {
        val weatherGradient = LocalWeatherGradient.current
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = weatherGradient.first(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            LocationDetailsContent(
                appScope = appScope,
                address = selectedAddress,
                city = selectedCity,
                country = selectedCountry,
                latLng = selectedLocation,
                viewModel = viewModel,
                onConfirm = {
                    selectedLocation?.let { onLocationSelected(it, selectedAddress) }
                    showBottomSheet = false
                },
                onDismiss = {
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion { showBottomSheet = false }
                },
                windUnit = windUnit,
                snackbarHostState = snackbarHostState
            )
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }
}
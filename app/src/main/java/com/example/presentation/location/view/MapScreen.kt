package com.example.presentation.component.location
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    onLocationSelected: (LatLng, String) -> Unit,
    initialLocation: LatLng = LatLng(30.0444, 31.2357),
    initialZoom: Float = 12f,
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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, initialZoom)
    }

    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            place.latLng?.let { latLng ->
                viewModel.onPlaceSelected(latLng, place.address ?: place.name ?: "")
                showBottomSheet = true
            }
        }
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
                                color = Color(0xFF2C3E6B).copy(alpha = 0.92f),
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

        FloatingActionButton(
            onClick = {
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                ).build(context)
                autocompleteLauncher.launch(intent)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp),
            containerColor = Color(0xFF1B2A4A),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }

        FloatingActionButton(
            onClick = { nav.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp),
            containerColor = Color(0xFF1B2A4A),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1B2A4A),
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
                windUnit  =windUnit,
                snackbarHostState = snackbarHostState
            )
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f)
            )
        }
    }
}

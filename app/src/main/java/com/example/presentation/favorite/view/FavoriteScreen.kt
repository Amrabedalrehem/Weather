package com.example.presentation.favorite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.data.model.entity.FavouriteLocationCache
import com.example.presentation.component.favourites.FavouriteCard
import com.example.presentation.component.helper.CustomToast
import com.example.presentation.component.helper.ToastType
import com.example.presentation.component.helper.rememberToastState
import com.example.presentation.favorite.viewmodel.FavUiEvent
import com.example.presentation.favorite.viewmodel.FavoritesViewModel
import com.example.weather.R
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel,
    onFavouriteClick: (FavouriteLocationCache) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val favourites   by viewModel.favourites.collectAsState()
    val activeAlarms by viewModel.alarms.collectAsState()
    val scope        = rememberCoroutineScope()
    val toastState   = rememberToastState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FavUiEvent.ShowCard -> toastState.show(
                    message = event.message,
                    type    = ToastType.INFO
                )
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredFavourites = favourites.filter {
        it.city.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
        ) {
            if (favourites.isNotEmpty()) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier      = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder   = { Text("Search city...", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.White,
                        cursorColor          = Color.White,
                        focusedBorderColor   = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    ),
                    shape      = RoundedCornerShape(25.dp),
                    singleLine = true
                )
            }

            when {
                favourites.isEmpty() -> {
                    Column(
                        modifier            = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(R.raw.cat)
                        )
                        LottieAnimation(
                            composition = composition,
                            iterations  = LottieConstants.IterateForever,
                            modifier    = Modifier.size(220.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text("No favorites yet", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap the button to add a city", fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }

                filteredFavourites.isEmpty() -> {
                    Column(
                        modifier            = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No cities match your search", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier            = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredFavourites) { location ->
                            FavouriteCard(
                                location         = location,
                                activeAlarms     = activeAlarms,
                                onClick          = { onFavouriteClick(location) },
                                onDeleteWithUndo = {
                                    viewModel.markForDeletion(location)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message     = "${location.city} removed ⭐",
                                            actionLabel = "Undo",
                                            duration    = SnackbarDuration.Short
                                        )
                                        when (result) {
                                            SnackbarResult.Dismissed       -> viewModel.confirmDelete(location)
                                            SnackbarResult.ActionPerformed -> viewModel.undoDelete(location)
                                        }
                                    }
                                },
                                onAddAlarm     = { alarm -> viewModel.addAlarm(alarm) },
                                onDisableAlarm = { alarm -> viewModel.disableAlarm(alarm) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }

        CustomToast(state = toastState)
    }
}
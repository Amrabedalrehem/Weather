package com.example.presentation.favorite.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.data.model.entity.FavouriteLocation
import com.example.presentation.component.favourites.FavouriteCard
import com.example.presentation.favorite.viewmodel.FavoritesViewModel
import com.example.weather.R
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel,
    onFavouriteClick: (FavouriteLocation) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val favourites by viewModel.favourites.collectAsState()
     val scope = rememberCoroutineScope()

    if (favourites.isEmpty()) {
        Column(
            modifier = Modifier.background(brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF6B8CB5),
                    Color(0xFF8BA5C9),
                    Color(0xFF9FB5D1)
                )
            )).fillMaxSize(),
             verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

             val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.cat)
            )

            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(220.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "No favorites yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tap the button to add a city",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
       else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6B8CB5),
                            Color(0xFF8BA5C9),
                            Color(0xFF9FB5D1)
                        )
                    ))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favourites) { location ->
                    FavouriteCard(
                        location = location,
                        onClick = { onFavouriteClick(location) },
                        onDeleteWithUndo = {
                            viewModel.markForDeletion(location)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "${location.city} removed ⭐",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                when (result) {
                                    SnackbarResult.Dismissed -> viewModel.confirmDelete(location)
                                    SnackbarResult.ActionPerformed -> viewModel.undoDelete(location)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
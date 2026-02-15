package com.example.presentation.view.logo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weather.R
import kotlinx.coroutines.delay


@Composable
fun LogoScreen(modifier: Modifier = Modifier) {
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
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(500)) + scaleOut(animationSpec = tween(500))
        ) {
            MyLottieAnimation(size = 280.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(1000, delayMillis = 500)
            ) + slideInVertically(
                animationSpec = tween(1000, delayMillis = 500),
                initialOffsetY = { it / 2 }
            )
        ) {
            Text(
                text = "Weather",
                fontSize = 42.sp,
                color = Color(0xFF4A6A8F),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MyLottieAnimation(size: Dp = 200.dp) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.cloud_and_sun_animation)
    )

    LottieAnimation(
        composition = composition,
        modifier = Modifier.size(size),
        iterations = LottieConstants.IterateForever
    )
}
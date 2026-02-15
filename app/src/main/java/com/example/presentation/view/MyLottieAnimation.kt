package com.example.presentation.view

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weather.R


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
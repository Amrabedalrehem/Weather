package com.example.presentation.utils
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = com.example.presentation.theme.LocalWeatherGradient.current
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition()

            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue  = 360f,
                animationSpec = infiniteRepeatable(
                    animation  = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Icon(
                painter           = painterResource(id = R.drawable.sunny),
                contentDescription = null,
                modifier          = Modifier.size(80.dp).rotate(rotation),
                tint              = Color(0xFFFCD34D)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = stringResource(R.string.loading_weather),
                fontSize   = 18.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator(
                modifier    = Modifier.size(40.dp),
                color       = MaterialTheme.colorScheme.onBackground,
                strokeWidth = 3.dp
            )
        }
    }
}
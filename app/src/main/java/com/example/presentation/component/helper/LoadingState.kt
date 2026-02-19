package com.example.presentation.component.helper

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
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF3B82F6),
                        Color(0xFF60A5FA),
                        Color(0xFF93C5FD)
                    )
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
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Icon(
                 painter = painterResource(id = R.drawable.wind),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(rotation),
                tint = Color(0xFFFCD34D)
            )

            Spacer(modifier = Modifier.height(24.dp))

             Text(
                text = "Loading Weather...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

             CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}
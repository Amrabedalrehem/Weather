package com.example.presentation.permission.view
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.MyLottieAnimation
import kotlinx.coroutines.delay
@Composable
fun PermissionScreen(modifier: Modifier = Modifier, onNavigateToHome: () -> Unit) {
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
            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(500)) + scaleOut(animationSpec = tween(500))
        ) {
            MyLottieAnimation(size = 200.dp)
        }

        Spacer(modifier = Modifier.height(40.dp))

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
            enter = fadeIn(
                animationSpec = tween(1000, delayMillis = 700)
            )
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
            enter = fadeIn(
                animationSpec = tween(1000, delayMillis = 900)
            )
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
            enter = fadeIn(
                animationSpec = tween(1000, delayMillis = 1100)
            ) + scaleIn(
                animationSpec = tween(1000, delayMillis = 1100)
            )
        ) {
             Button(
                 onClick = onNavigateToHome,
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
                     text = "Allow Permission",
                     fontSize = 18.sp,
                     fontWeight = FontWeight.Bold,
                     letterSpacing = 0.5.sp
                 )
             }
        }

        Spacer(modifier = Modifier.height(20.dp))

         AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(1000, delayMillis = 1300)
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 20.sp,

                color = Color(0xFFE0E8F0),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onNavigateToHome)
            )
        }
    }
}








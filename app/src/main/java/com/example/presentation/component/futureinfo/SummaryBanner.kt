package com.example.presentation.component.futureinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.futureinfo.viewmodel.BadWeatherDay
import com.example.presentation.futureinfo.viewmodel.Severity


@Composable
fun SummaryBanner(days: List<BadWeatherDay>) {
    val extremeCount  = days.count { it.severity == Severity.EXTREME }
    val highCount     = days.count { it.severity == Severity.HIGH }
    val moderateCount = days.count { it.severity == Severity.MODERATE }

    val bannerColor = when {
        extremeCount > 0 -> Brush.horizontalGradient(listOf(Color(0xFF7F1D1D), Color(0xFFB91C1C)))
        highCount    > 0 -> Brush.horizontalGradient(listOf(Color(0xFF92400E), Color(0xFFD97706)))
        else             -> Brush.horizontalGradient(listOf(Color(0xFF1E3A5F), Color(0xFF2563EB)))
    }

    val emoji = when {
        extremeCount > 0 -> "🚨"
        highCount    > 0 -> "⚠️"
        else             -> "🌧️"
    }

    val message = when {
        extremeCount > 0 -> "$extremeCount extreme weather day${if (extremeCount > 1) "s" else ""} ahead"
        highCount    > 0 -> "$highCount bad weather day${if (highCount > 1) "s" else ""} ahead"
        else             -> "${days.size} day${if (days.size > 1) "s" else ""} with rough weather"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bannerColor)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(message, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "Plan your week accordingly",
                    color    = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

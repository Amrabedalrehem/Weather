package com.example.presentation.component.futureinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.presentation.futureinfo.viewmodel.BadWeatherDay
import com.example.presentation.futureinfo.viewmodel.Severity


@Composable
fun DayCard(day: BadWeatherDay) {
    val (severityColor, severityLabel, severityEmoji) = when (day.severity) {
        Severity.EXTREME  -> Triple(Color(0xFFEF4444), "EXTREME",  "🔴")
        Severity.HIGH     -> Triple(Color(0xFFF59E0B), "HIGH",     "🟠")
        Severity.MODERATE -> Triple(Color(0xFF3B82F6), "MODERATE", "🔵")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1E3357))
    ) {
        Row(
            modifier            = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(severityColor)
                )
                Spacer(Modifier.width(10.dp))

                AsyncImage(
                    model       = "https://openweathermap.org/img/wn/${day.icon}@2x.png",
                    contentDescription = day.description,
                    modifier    = Modifier.size(44.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text       = day.dayName,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                    Text(
                        text     = day.date,
                        color    = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    Text(
                        text     = day.description,
                        color    = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = "${day.temp}°",
                    color      = Color.White,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(severityColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text       = "$severityEmoji $severityLabel",
                        color      = severityColor,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
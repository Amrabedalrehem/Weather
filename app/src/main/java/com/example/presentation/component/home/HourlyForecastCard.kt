package com.example.presentation.component.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.dto.HourlyForecastResponse


@Composable
fun HourlyForecastCard(
    time: String,
    main: String,
    temperature: Double,
    icon: String,
    temp_max: Double,
    temp_min: Double,
    speed: Double,
    windUnit: String = "m/s"
) {
    val displaySpeed = if (windUnit == "mph") {
        "${"%.1f".format(speed * 2.23694)} mph"
    } else {
        "$speed m/s"
    }
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            AsyncImage(
                model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "$temperature °",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "↑${"%.0f".format(temp_max)}°",
                    fontSize = 10.sp,
                    color = Color(0xFFFCD34D),
                    maxLines = 1
                )
                Text(
                    text = "↓${"%.0f".format(temp_min)}°",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = displaySpeed,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Text(
                text = main,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HourlyForecastSection(hourlyForecast: HourlyForecastResponse,
                          windUnit: String = "m/s"
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Hourly Forecast",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hourlyForecast.hourly.size) { hour ->
                HourlyForecastCard(
                    time = hourlyForecast.hourly[hour].time.toHourOnly(),
                    main = hourlyForecast.hourly[hour].weather[0].main,
                    temperature = hourlyForecast.hourly[hour].main.temp,
                    icon = hourlyForecast.hourly[hour].weather[0].icon,
                    temp_max = hourlyForecast.hourly[hour].main.tempMax,
                    temp_min = hourlyForecast.hourly[hour].main.tempMin,
                    speed = hourlyForecast.hourly[hour].wind.speed,
                    windUnit = windUnit
                )
            }
        }
    }
}

fun String.toHourOnly(): String {

    return this.split(" ")[1].substring(0, 5)
}
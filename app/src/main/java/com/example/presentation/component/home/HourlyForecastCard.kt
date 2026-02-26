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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.presentation.utils.getWeatherIcon
import com.example.data.model.dto.HourlyForecastResponse
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import com.example.presentation.utils.toArabicDigits
import com.example.presentation.utils.localizeWeatherMain


@Composable
fun HourlyForecastCard(
    time: String,
    main: String,
    temperature: Double,
    icon: String,
    temp_max: Double,
    temp_min: Double,
    speed: Double,
    windUnit: String = "ms"
) {
    val displaySpeed = if (windUnit == "mph") {
        stringResource(R.string.wind_speed_mph, "%.1f".format(speed * 2.23694)).toArabicDigits()
    } else {
        stringResource(R.string.wind_speed_ms, speed.toString()).toArabicDigits()
    }
    Card(
        shape = RoundedCornerShape(40.dp),
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
                text = time.toArabicDigits(),
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Image(
                painter = painterResource(id = getWeatherIcon(icon)),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "$temperature °".toArabicDigits(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "↑${"%.0f".format(temp_max)}°".toArabicDigits(),
                    fontSize = 10.sp,
                    color = Color(0xFFFCD34D),
                    maxLines = 1
                )
                Text(
                    text = "↓${"%.0f".format(temp_min)}°".toArabicDigits(),
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
                text = localizeWeatherMain(main),
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
fun HourlyForecastSection(hourlyForecast: HourlyForecastResponse?,
                          windUnit: String? = "ms"
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.hourly_forecast),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hourlyForecast?.hourly?.size ?: 0) { hour ->
                HourlyForecastCard(
                    time = hourlyForecast?.hourly[hour]?.time?.toHourOnly() ?: "",
                    main = hourlyForecast?.hourly[hour]?.weather[0]?.main ?: "",
                    temperature = hourlyForecast?.hourly[hour]?.main?.temp ?: 0.0,
                    icon = hourlyForecast?.hourly[hour]?.weather[0]?.icon ?: "",
                    temp_max = hourlyForecast?.hourly[hour]?.main?.tempMax ?:0.0,
                    temp_min = hourlyForecast?.hourly[hour]?.main?.tempMin?:0.0 ,
                    speed = hourlyForecast?.hourly[hour]?.wind?.speed ?: 0.0,
                    windUnit = windUnit?:"ms"
                )
            }
        }
    }
}

fun String.toHourOnly(): String {

    return this.split(" ")[1].substring(0, 5)
}
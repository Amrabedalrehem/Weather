package com.example.presentation.component.home
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.dto.CurrentWeatherDto
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import com.example.presentation.utils.getWeatherIcon
import com.example.presentation.utils.toArabicDigits
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun CurrentWeatherSection(currentWeather: CurrentWeatherDto?) {


    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 24.dp).padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = currentWeather?.name + " " + currentWeather?.sys?.country,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

         Text(
            text = getCurrentDate().toArabicDigits(),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Text(
            text = getCurrentTime().toArabicDigits(),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

         Box(
            modifier = Modifier
                .size(180.dp).scale(scale).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
               contentAlignment = Alignment.Center
        ) {
             Image(
                 painter = painterResource(id = getWeatherIcon(currentWeather?.weather?.firstOrNull()?.icon)),
                 contentDescription = stringResource(R.string.weather_icon),
                 modifier = Modifier.size(100.dp)
             )
         }


             Spacer(modifier = Modifier.height(32.dp))

         Text(
             text = "${currentWeather?.main?.temp?.toInt()}°".toArabicDigits(),
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            lineHeight = 96.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        currentWeather?.weather?.get(0)?.main?.let {
            val localizedMain = when (it) {
                "Clear" -> stringResource(R.string.weather_clear)
                "Clouds" -> stringResource(R.string.weather_clouds)
                "Rain" -> stringResource(R.string.weather_rain)
                "Drizzle" -> stringResource(R.string.weather_drizzle)
                "Thunderstorm" -> stringResource(R.string.weather_thunderstorm)
                "Snow" -> stringResource(R.string.weather_snow)
                "Mist" -> stringResource(R.string.weather_mist)
                "Haze" -> stringResource(R.string.weather_haze)
                "Fog" -> stringResource(R.string.weather_fog)
                "Dust" -> stringResource(R.string.weather_dust)
                "Sand" -> stringResource(R.string.weather_sand)
                "Smoke" -> stringResource(R.string.weather_smoke)
                "Tornado" -> stringResource(R.string.weather_tornado)
                "Squall" -> stringResource(R.string.weather_squall)
                else -> it
            }
            Text(
                text = localizedMain,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Text(
            text = stringResource(R.string.feels_like, currentWeather?.weather?.get(0)?.description ?: "").toArabicDigits(),
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
@Composable
fun getCurrentDate(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy", Locale.getDefault())
    return current.format(formatter)
}

@Composable
fun getCurrentTime(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    return current.format(formatter)
}


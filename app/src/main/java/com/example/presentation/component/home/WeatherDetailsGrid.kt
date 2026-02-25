package com.example.presentation.component.home
 import androidx.compose.foundation.layout.Arrangement
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Row
 import androidx.compose.foundation.layout.Spacer
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.height
 import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.layout.size
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material3.Card
 import androidx.compose.material3.CardDefaults
 import androidx.compose.material3.Text
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.getValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp
 import com.airbnb.lottie.compose.LottieAnimation
 import com.airbnb.lottie.compose.LottieCompositionSpec
 import com.airbnb.lottie.compose.LottieConstants
 import com.airbnb.lottie.compose.rememberLottieComposition
 import com.example.data.model.dto.CurrentWeatherDto
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import com.example.presentation.component.helper.toArabicDigits

@Composable
fun WeatherDetailsGrid(weatherData: CurrentWeatherDto?
,   windUnit: String = "m/s"
) {
    val windSpeed = if (windUnit == "mph") {
        stringResource(R.string.wind_speed_mph, "%.1f".format(weatherData?.wind?.speed?.times(2.23694))).toArabicDigits()
    } else {
        stringResource(R.string.wind_speed_ms, weatherData?.wind?.speed.toString()).toArabicDigits()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
    {
        Text(
            text = stringResource(R.string.weather_details),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherDetailCard(
                icon = R.raw.humidity,
                label = stringResource(R.string.humidity),
                 value = "${weatherData?.main?.humidity}%".toArabicDigits(),
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon =  R.raw.windgust,
                label = stringResource(R.string.wind),
                value = windSpeed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherDetailCard(
                icon =R.raw.thermometercolder,
                label = stringResource(R.string.pressure),
                value = stringResource(R.string.pressure_hpa, weatherData?.main?.pressure.toString()).toArabicDigits(),
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = R.raw.cloud_and_sun_animation,
                label = stringResource(R.string.clouds),
                value = "${weatherData?.main?.feelsLike?.toInt()}°".toArabicDigits(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
fun WeatherDetailCard(
    icon:  Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(icon)
            )
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

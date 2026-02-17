package com.example.presentation.home.view.component
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
 import com.example.data.model.weather.CurrentWeatherDto
 import com.example.weather.R

@Composable
fun WeatherDetailsGrid(weatherData: CurrentWeatherDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
    {
        Text(
            text = "Weather Details",
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
                label = "Humidity",
                 value = "${weatherData.main.humidity}%",
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon =  R.raw.windgust,
                label = "Wind",
                value = "${weatherData.wind.speed} km/h",
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
                label = "Pressure",
                value = "${weatherData.main.pressure} hPa",
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = R.raw.cloud_and_sun_animation,
                label = "Clouds",
                value = "${weatherData.main.feelsLike.toInt()}°",
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

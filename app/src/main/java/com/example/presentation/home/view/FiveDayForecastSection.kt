 import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FiveDayForecastSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "5-Day Forecast",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        days.forEachIndexed { index, day ->
            FiveDayForecastCard(
                day = day,
                highTemp = "${30 + index}°",
                lowTemp = "${20 + index}°",
                icon = Icons.Default.Warning,
                humidity = "${60 + index}%",
                windSpeed = "${10 + index} km/h",
                pressure = "${1010 + index} hPa",
                clouds = "${15 + index * 5}%",
                description = "Clear Sky"
            )
            if (index < days.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FiveDayForecastCard(
    day: String,
    highTemp: String,
    lowTemp: String,
    icon: ImageVector,
    humidity: String,
    windSpeed: String,
    pressure: String,
    clouds: String,
    description: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
             Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFCD34D),
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = highTemp,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = " / ",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = lowTemp,
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                )
            }

             AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(500)) + expandVertically(tween(300)),
                exit = fadeOut(tween(500)) + shrinkVertically(tween(300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                     Divider(
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                     Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                     Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DayDetailItem(
                            icon = Icons.Default.Info,
                            label = "Humidity",
                            value = humidity,
                            modifier = Modifier.weight(1f)
                        )
                        DayDetailItem(
                            icon = Icons.Default.Info,
                            label = "Wind",
                            value = windSpeed,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DayDetailItem(
                            icon = Icons.Default.KeyboardArrowUp,
                            label = "Pressure",
                            value = pressure,
                            modifier = Modifier.weight(1f)
                        )
                        DayDetailItem(
                            icon = Icons.Default.KeyboardArrowUp,
                            label = "Clouds",
                            value = clouds,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
 @Composable
 fun DayDetailItem(
     icon: ImageVector,
     label: String,
     value: String,
     modifier: Modifier = Modifier
 ) {
     Box(
         modifier = modifier
             .size(width = 100.dp, height = 100.dp)
             .clip(RoundedCornerShape(12.dp))
             .background(Color.White.copy(alpha = 0.1f)),
         contentAlignment = Alignment.Center
     ) {
         Column(
             horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement = Arrangement.Center
         ) {
             Icon(
                 imageVector = icon,
                 contentDescription = null,
                 tint = Color.White.copy(alpha = 0.8f),
                 modifier = Modifier.size(20.dp)
             )
             Spacer(modifier = Modifier.height(6.dp))
             Text(
                 text = label,
                 fontSize = 11.sp,
                 color = Color.White.copy(alpha = 0.6f)
             )
             Spacer(modifier = Modifier.height(2.dp))
             Text(
                 text = value,
                 fontSize = 14.sp,
                 fontWeight = FontWeight.Bold,
                 color = Color.White
             )
         }
     }
 }

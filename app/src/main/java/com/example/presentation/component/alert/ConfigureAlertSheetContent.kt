package com.example.presentation.component.alert
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.weather.R
import com.example.presentation.utils.toArabicDigits
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureAlertSheetContent(
    selectedType: String,
    threshold: Float,
    onTypeChange: (String) -> Unit,
    onThresholdChange: (Float) -> Unit,
    datePickerState: DatePickerState,
    timePickerState: TimePickerState,
    notificationType: String,
    onNotificationTypeChange: (String) -> Unit,
    onShowTimePicker: () -> Unit,
    onDone: () -> Unit,
) {
     val gradientColors = com.example.presentation.theme.LocalWeatherGradient.current

    val textMain = MaterialTheme.colorScheme.onBackground
    val textSub  = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val textFaint= MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val alertTypes = listOf(
        stringResource(R.string.alert_rain),
        stringResource(R.string.alert_wind),
        stringResource(R.string.alert_temp),
        stringResource(R.string.alert_storm)
    )
    val alertIcons = mapOf(
        "Rain" to R.raw.rainicon,
        "Wind" to R.raw.cloud,
        "Temp" to R.raw.cloud_and_sun_animation,
        "Storm" to R.raw.speed
    )

    val unit = when (selectedType) {
        "Temp" -> "°C"
        "Wind" -> "km/h"
        "Rain" -> "mm"
        else -> ""
    }

    val formattedTime by remember(timePickerState.hour, timePickerState.minute) {
        derivedStateOf {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                set(Calendar.MINUTE, timePickerState.minute)
            }
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(gradientColors))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .background(textMain.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.storm_alert_info),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textMain
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = textMain.copy(alpha = 0.1f))
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            alertTypes.forEach { type ->
                val isSelected = type == selectedType

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onTypeChange(type)
                        onThresholdChange(0f)
                    }
                ) {

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                if (isSelected)
                                    textMain.copy(alpha = 0.2f)
                                else
                                    textMain.copy(alpha = 0.08f),
                                RoundedCornerShape(16.dp)
                            )
                            .then(
                                if (isSelected)
                                    Modifier.border(2.dp, tertiaryColor, RoundedCornerShape(16.dp))
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(alertIcons[type] ?: R.raw.notification)
                        )

                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = type,
                        fontSize = 13.sp,
                        color = if (isSelected) tertiaryColor else textFaint,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = textMain.copy(alpha = 0.1f))
        Spacer(Modifier.height(20.dp))

        if (selectedType != "Storm") {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.threshold_level),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textMain
                )

                Text(
                    text = "${threshold.toInt()} $unit".toArabicDigits(),
                    fontSize = 16.sp,
                    color = tertiaryColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            val maxValue = when (selectedType) {
                "Temp" -> 60f
                "Wind" -> 150f
                "Rain" -> 50f
                else -> 100f
            }

            Slider(
                value = threshold,
                onValueChange = onThresholdChange,
                valueRange = 0f..maxValue,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = tertiaryColor,
                    activeTrackColor = tertiaryColor,
                    inactiveTrackColor = textMain.copy(alpha = 0.3f)
                )
            )

        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = textMain.copy(alpha = 0.1f))
            ) {
                Text(
                    text = stringResource(R.string.storm_alert_info),
                    fontSize = 14.sp,
                    color = textSub,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = textMain.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent,
                titleContentColor = textMain,
                headlineContentColor = textMain,
                weekdayContentColor = textMain.copy(alpha = 0.6f),
                dayContentColor = textMain,
                selectedDayContainerColor = tertiaryColor,
                todayContentColor = tertiaryColor,
                todayDateBorderColor = tertiaryColor
            )
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = textMain.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.choose_time),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = textSub
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onShowTimePicker,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, textMain.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = textMain)
        ) {
            Text(
                text = "🕐  $formattedTime".toArabicDigits(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textMain
            )
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = textMain.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.choose_preferred_option),
            color = tertiaryColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val typeLabels = mapOf(
                "Alert" to stringResource(R.string.alert),
                "Notification" to stringResource(R.string.notification)
            )

            typeLabels.forEach { (key, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = notificationType == key,
                        onClick = { onNotificationTypeChange(key) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = tertiaryColor,
                            unselectedColor = tertiaryColor.copy(alpha = 0.5f)
                        )
                    )
                    Text(
                        text = label,
                        color = textMain,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = tertiaryColor)
        ) {
            Text(
                text = stringResource(R.string.create_alert),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
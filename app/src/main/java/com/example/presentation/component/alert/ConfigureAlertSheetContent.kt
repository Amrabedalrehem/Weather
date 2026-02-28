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
    onDone: () -> Unit
) {
    val gradientColors = com.example.presentation.theme.LocalWeatherGradient.current

    val alertTypes = listOf("Rain", "Wind", "Temp", "Storm")

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
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.storm_alert_info),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
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
                                    Color.White.copy(alpha = 0.2f)
                                else
                                    Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(16.dp)
                            )
                            .then(
                                if (isSelected)
                                    Modifier.border(
                                        2.dp,
                                        MaterialTheme.colorScheme.tertiary,
                                        RoundedCornerShape(16.dp)
                                    )
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                alertIcons[type] ?: R.raw.notification
                            )
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
                        color = if (isSelected)
                            MaterialTheme.colorScheme.tertiary
                        else
                            Color.White.copy(alpha = 0.7f),
                        fontWeight = if (isSelected)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
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
                    color = Color.White
                )

                Text(
                    text = "${threshold.toInt()} $unit".toArabicDigits(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary,
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
                    thumbColor = MaterialTheme.colorScheme.tertiary,
                    activeTrackColor = MaterialTheme.colorScheme.tertiary,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )

        } else {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = stringResource(R.string.storm_alert_info),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.White.copy(alpha = 0.6f),
                dayContentColor = Color.White,
                selectedDayContainerColor = MaterialTheme.colorScheme.tertiary,
                todayContentColor = MaterialTheme.colorScheme.tertiary,
                todayDateBorderColor = MaterialTheme.colorScheme.tertiary
            )
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.choose_time),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onShowTimePicker,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = "🕐  $formattedTime".toArabicDigits(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.choose_preferred_option),
            color = MaterialTheme.colorScheme.tertiary,
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
                            selectedColor = Color.White,
                            unselectedColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                    Text(
                        text = label,
                        color = Color.White,
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
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
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
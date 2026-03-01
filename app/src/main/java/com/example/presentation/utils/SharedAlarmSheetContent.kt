package com.example.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedAlarmSheetContent(
    title: String,
    subtitle: String,
    datePickerState: DatePickerState,
    timePickerState: TimePickerState,
    selectedType: String,
    onTypeChange: (String) -> Unit,
    onShowTimePicker: () -> Unit,
    onDone: () -> Unit
) {
    val formattedTime by remember(timePickerState.hour, timePickerState.minute) {
        derivedStateOf {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                set(Calendar.MINUTE, timePickerState.minute)
            }
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
        }
    }

     val gradientColors = com.example.presentation.theme.LocalWeatherGradient.current

     val textMain     = MaterialTheme.colorScheme.onBackground
    val textSub      = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val textFaint    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    val tertiaryColor= MaterialTheme.colorScheme.tertiary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(gradientColors))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .background(textMain.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textMain
        )

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

        Spacer(Modifier.height(12.dp))
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
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = textMain),
            border = androidx.compose.foundation.BorderStroke(1.dp, textMain.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🕐  $formattedTime".toArabicDigits(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textMain
            )
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = textMain.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        Text(
            text = subtitle,
            color = textFaint,
            fontSize = 14.sp
        )

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
                        selected = selectedType == key,
                        onClick = { onTypeChange(key) },
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

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = tertiaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.done),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun buildCalendar(
    datePickerState: DatePickerState,
    timePickerState: TimePickerState
): Calendar {
    val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()

    return Calendar.getInstance().apply {
        timeInMillis = selectedDate
        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
        set(Calendar.MINUTE, timePickerState.minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
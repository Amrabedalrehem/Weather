package com.example.presentation.component.favourites
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import com.example.presentation.utils.SharedAlarmSheetContent
import com.example.presentation.utils.buildCalendar
import com.example.presentation.utils.toArabicDigits
import com.example.presentation.utils.localizeWeatherMain
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteCard(
    location: FavouriteLocationCache,
    activeAlarms: List<AlarmEntity>,
    onClick: () -> Unit,
    onDeleteWithUndo: () -> Unit,
    onAddAlarm: (AlarmEntity) -> Unit,
    onDisableAlarm: (AlarmEntity) -> Unit
) {
    val activeAlarm    = activeAlarms.firstOrNull { it.city == location.city && it.isActive }
    val isAlarmEnabled = activeAlarm != null

    var showAlarmSheet    by remember { mutableStateOf(false) }
    var showDisableDialog by remember { mutableStateOf(false) }
    var showTimePicker    by remember { mutableStateOf(false) }
    var selectedType      by remember { mutableStateOf("Alert") }
    val sheetState        = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState   = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val timePickerState   = rememberTimePickerState()

    val formattedTime by remember(timePickerState.hour, timePickerState.minute) {
        derivedStateOf {
            val cal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                set(java.util.Calendar.MINUTE, timePickerState.minute)
            }
            java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(cal.time)
        }
    }

    Card(
        shape    = RoundedCornerShape(28.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {

            IconButton(
                onClick  = { onDeleteWithUndo() },
                modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.minuscircle))
                LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(50.dp))
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text       = "${location.currentWeather?.name} (${location.city})",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text     = location.currentWeather?.sys?.country ?: stringResource(R.string.unknown),
                    fontSize = 16.sp,
                    color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = localizeWeatherMain(location.currentWeather?.weather?.get(0)?.main ?: stringResource(R.string.unknown)),
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                    )
                    Text(
                        text     = stringResource(R.string.feels_like, (location.currentWeather?.main?.feelsLike?.toInt() ?: "--").toString()).toArabicDigits(),
                        fontSize = 16.sp,
                        color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.notification))
                        LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(50.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text       = if (isAlarmEnabled) stringResource(R.string.alarm_set) else stringResource(R.string.no_alarm_set),
                                color      = if (isAlarmEnabled) Color(0xFF3B82F6) else MaterialTheme.colorScheme.onBackground,
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text     = if (isAlarmEnabled) stringResource(R.string.tap_switch_disable) else stringResource(R.string.stay_ready),
                                color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Switch(
                        checked         = isAlarmEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) showAlarmSheet = true
                            else showDisableDialog = true
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = Color(0xFF1976D2),
                            checkedTrackColor   = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedThumbColor = Color(0xFF1976D2),
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }

    if (showDisableDialog) {
        AlertDialog(
            onDismissRequest = { showDisableDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.disable_alarm_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.disable_alarm_msg,
                        location.city
                    ),
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        activeAlarm?.let { onDisableAlarm(it) }
                        showDisableDialog = false
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.disable))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDisableDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showTimePicker) {
        Dialog(
            onDismissRequest = { showTimePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.select_time),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TimePicker(
                        state = timePickerState
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showTimePicker = false }
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showTimePicker = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.ok),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAlarmSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAlarmSheet = false },
            sheetState       = sheetState,
            containerColor   = MaterialTheme.colorScheme.primary,
        ) {
            SharedAlarmSheetContent(
                title = stringResource(R.string.set_alarm_for, location.city),
                subtitle = stringResource(R.string.weather_updates_city, location.city),
                datePickerState = datePickerState,
                timePickerState = timePickerState,
                selectedType = selectedType,
                onTypeChange = { selectedType = it },
                onShowTimePicker = { showTimePicker = true },
                onDone = {
                    val calendar = buildCalendar(datePickerState, timePickerState)
                    onAddAlarm(
                        AlarmEntity(
                            city         = location.city,
                            latitude     = location.lat,
                            longitude    = location.lon,
                            timeInMillis = calendar.timeInMillis,
                            type         = selectedType
                        )
                    )
                    showAlarmSheet = false
                }
            )
        }
    }
}
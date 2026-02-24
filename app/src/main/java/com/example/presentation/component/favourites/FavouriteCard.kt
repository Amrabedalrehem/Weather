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
            val h    = timePickerState.hour
            val m    = timePickerState.minute
            val amPm = if (h < 12) "AM" else "PM"
            val hour = if (h % 12 == 0) 12 else h % 12
            "%02d:%02d %s".format(hour, m, amPm)
        }
    }

     Card(
        shape    = RoundedCornerShape(28.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1B2A4A)),
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
                    color      = Color.White
                )
                Text(
                    text     = location.currentWeather?.sys?.country ?: "unknown",
                    fontSize = 16.sp,
                    color    = Color.White.copy(alpha = 0.7f)
                )

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = location.currentWeather?.weather?.get(0)?.main ?: "unknown",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color      = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text     = "Feels like ${location.currentWeather?.main?.feelsLike?.toInt() ?: "--"}°",
                        fontSize = 16.sp,
                        color    = Color.White.copy(alpha = 0.7f)
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
                                text       = if (isAlarmEnabled) "Alarm set" else "No alarm set",
                                color      = if (isAlarmEnabled) Color(0xFF3B82F6) else Color.White,
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text     = if (isAlarmEnabled) "Tap switch to disable" else "Stay ready!",
                                color    = Color.White.copy(alpha = 0.6f),
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
                            checkedThumbColor   = Color(0xFF2E4A6B),
                            checkedTrackColor   = Color.White,
                            uncheckedThumbColor = Color(0xFF2E4A6B),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }

     if (showDisableDialog) {
        AlertDialog(
            onDismissRequest = { showDisableDialog = false },
            containerColor   = Color(0xFF1B2A4A),
            title = {
                Text("Disable Alarm?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Are you sure you want to disable the alarm for ${location.city}?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        activeAlarm?.let { onDisableAlarm(it) }
                        showDisableDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    shape  = RoundedCornerShape(8.dp)
                ) {
                    Text("Disable", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDisableDialog = false },
                    shape   = RoundedCornerShape(8.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3B82F6))
                ) {
                    Text("Cancel")
                }
            }
        )
    }

     if (showTimePicker) {
        Dialog(
            onDismissRequest = { showTimePicker = false },
            properties       = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFF1B2A4A)),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Select Time", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(20.dp))

                     TimePicker(
                        state  = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor                       = Color(0xFF2E4A6B),
                            clockDialSelectedContentColor        = Color.White,
                            clockDialUnselectedContentColor      = Color.White.copy(alpha = 0.7f),
                            selectorColor                        = Color(0xFF3B82F6),
                            containerColor                       = Color(0xFF1B2A4A),
                            periodSelectorBorderColor            = Color(0xFF3B82F6),
                            timeSelectorSelectedContainerColor   = Color(0xFF3B82F6),
                            timeSelectorUnselectedContainerColor = Color(0xFF2E4A6B),
                            timeSelectorSelectedContentColor     = Color.White,
                            timeSelectorUnselectedContentColor   = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showTimePicker = false },
                            colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape   = RoundedCornerShape(8.dp)
                        ) {
                            Text("OK", color = Color.White)
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
            containerColor   = Color(0xFF1B2A4A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text       = "Set Alarm for ${location.city}",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                DatePicker(
                    state  = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor            = Color(0xFF1B2A4A),
                        titleContentColor         = Color.White,
                        headlineContentColor      = Color.White,
                        weekdayContentColor       = Color.White.copy(alpha = 0.6f),
                        dayContentColor           = Color.White,
                        selectedDayContainerColor = Color(0xFF3B82F6),
                        todayContentColor         = Color(0xFF3B82F6),
                        todayDateBorderColor      = Color(0xFF3B82F6)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Choose Time", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                 OutlinedButton(
                    onClick  = { showTimePicker = true },
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🕐  $formattedTime", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text     = "Would you like to receive weather updates for ${location.city} via alerts or notifications?",
                    color    = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text       = "Choose your preferred option to stay informed!",
                    color      = Color(0xFF3B82F6),
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Alert", "Notification").forEach { type ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedType == type, onClick = { selectedType = type })
                            Text(text = type, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE,      timePickerState.minute)
                            set(Calendar.SECOND,      0)
                            set(Calendar.MILLISECOND, 0)
                        }
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
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text("Done", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
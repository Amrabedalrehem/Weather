package com.example.presentation.alarms.view
import com.example.presentation.utils.ToastType
import com.example.presentation.component.alert.AlarmCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.example.data.model.entity.AlarmEntity
import com.example.presentation.alarms.viewmodel.AlarmViewModel
import com.example.presentation.utils.AlarmUiEvent
import com.example.presentation.utils.CustomToast
import com.example.presentation.utils.rememberToastState
import com.example.weather.R
import androidx.compose.ui.res.stringResource
import com.example.presentation.utils.SharedAlarmSheetContent
import com.example.presentation.utils.toArabicDigits
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel,
    onRequestAddAlarm: (() -> Unit) -> Unit
) {
    val alarms by viewModel.alarms.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val toastState = rememberToastState()

    var showAddSheet by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("Alert") }
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val timePickerState = rememberTimePickerState()
    var showAddTimePicker by remember { mutableStateOf(false) }

    var showEditSheet by remember { mutableStateOf(false) }
    var alarmToEdit by remember { mutableStateOf<AlarmEntity?>(null) }
    var editSelectedType by remember { mutableStateOf("Alert") }
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editDateState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val editTimeState = rememberTimePickerState()
    var showEditTimePicker by remember { mutableStateOf(false) }

    var showResetDialog by remember { mutableStateOf(false) }
    var alarmToReset by remember { mutableStateOf<AlarmEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AlarmUiEvent.ShowCard -> toastState.show(
                    message = event.message,
                    type = when (event.type.name) {
                        "SUCCESS" -> ToastType.SUCCESS
                        "ERROR" -> ToastType.ERROR
                        else -> ToastType.INFO
                    }
                )
            }
        }
    }

    onRequestAddAlarm { showAddSheet = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = com.example.presentation.theme.LocalWeatherGradient.current
                )
            )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = com.example.presentation.theme.LocalWeatherGradient.current
                    )
                )
        ) {
            if (!alarms.isEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alarms, key = { it.id }) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            onDelete = { alarmToReset = alarm; showResetDialog = true },
                            onEdit = {
                                alarmToEdit = alarm
                                editSelectedType = alarm.type
                                showEditSheet = true
                            }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.notification))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(220.dp)
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        stringResource(R.string.journey_quiet),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.no_weather_alarms),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }

        CustomToast(state = toastState)
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = addSheetState,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            val currentLocationText = stringResource(R.string.current_location)
            SharedAlarmSheetContent(
                title = stringResource(R.string.choose_date_time),
                subtitle = stringResource(R.string.weather_updates_question),
                datePickerState = datePickerState,
                timePickerState = timePickerState,
                selectedType = selectedType,
                onTypeChange = { selectedType = it },
                onShowTimePicker = { showAddTimePicker = true },
                onDone = {
                    val calendar = buildCalendar(datePickerState, timePickerState)
                    viewModel.addAlarm(
                        AlarmEntity(
                            city = currentLocationText,
                            latitude = currentLocation.first,
                            longitude = currentLocation.second,
                            timeInMillis = calendar.timeInMillis,
                            type = selectedType
                        )
                    )
                    showAddSheet = false
                }
            )
        }
    }

    if (showAddTimePicker) {
        ClockPickerDialog(
            state = timePickerState,
            onDismiss = { showAddTimePicker = false },
            onConfirm = { showAddTimePicker = false }
        )
    }

    if (showEditSheet && alarmToEdit != null) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = editSheetState,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            SharedAlarmSheetContent(
                title = stringResource(R.string.edit_alarm),
                subtitle = stringResource(R.string.weather_updates_question),
                datePickerState = editDateState,
                timePickerState = editTimeState,
                selectedType = editSelectedType,
                onTypeChange = { editSelectedType = it },
                onShowTimePicker = { showEditTimePicker = true },
                onDone = {
                    val calendar = buildCalendar(editDateState, editTimeState)
                    alarmToEdit?.let { old ->
                        viewModel.editAlarm(
                            old = old,
                            new = old.copy(
                                timeInMillis = calendar.timeInMillis,
                                type = editSelectedType
                            )
                        )
                    }
                    showEditSheet = false
                }
            )
        }
    }

    if (showEditTimePicker) {
        ClockPickerDialog(
            state = editTimeState,
            onDismiss = { showEditTimePicker = false },
            onConfirm = { showEditTimePicker = false }
        )
    }

    if (showResetDialog && alarmToReset != null) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.delete_alarm_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.delete_alarm_msg,
                        alarmToReset?.city ?: ""
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        alarmToReset?.let { viewModel.deleteAlarm(it) }
                        showResetDialog = false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun buildCalendar(
    datePickerState: DatePickerState,
    timePickerState: TimePickerState
): Calendar = Calendar.getInstance().apply {
    timeInMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
    set(Calendar.MINUTE, timePickerState.minute)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockPickerDialog(
    state: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
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

                TimePicker(state = state)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onConfirm,
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
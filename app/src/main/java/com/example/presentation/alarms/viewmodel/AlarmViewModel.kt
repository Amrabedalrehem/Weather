package com.example.presentation.alarms.viewmodel
import  com.example.presentation.component.helper.AlarmUiEvent
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.alarm.AlarmScheduler
import com.example.data.model.entity.AlarmEntity
import com.example.presentation.component.helper.ToastType
 import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AlarmViewModel(
    private val repository: Repository,
    private val context: Context
) : ViewModel() {

    private val alarmScheduler = AlarmScheduler(context)

    private val _uiEvent = MutableSharedFlow<AlarmUiEvent>()
    val uiEvent: SharedFlow<AlarmUiEvent> = _uiEvent

    val currentLocation = combine(
        repository.latitude,
        repository.longitude
    ) { lat, lon -> Pair(lat, lon) }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(0.0, 0.0)
        )

    val currentCity: StateFlow<String> = repository.locationType
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val alarms: StateFlow<List<AlarmEntity>> = repository.getAllAlarms()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            if (alarm.timeInMillis <= System.currentTimeMillis()) {
                _uiEvent.emit(AlarmUiEvent.ShowCard("Please choose a future time", ToastType.ERROR))
                return@launch
            }
            val id = repository.insertAlarm(alarm)
            alarmScheduler.schedule(alarm.copy(id = id.toInt()))
            _uiEvent.emit(AlarmUiEvent.ShowCard("Alarm set successfully", ToastType.SUCCESS))
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm)
            repository.deleteAlarm(alarm)
        }
    }

    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            if (alarm.isActive) {
                alarmScheduler.cancel(alarm)
            } else {
                alarmScheduler.schedule(alarm)
            }
            repository.toggleAlarm(alarm.id, !alarm.isActive)
        }
    }

    fun editAlarm(old: AlarmEntity, new: AlarmEntity) {
        viewModelScope.launch {
            if (new.timeInMillis <= System.currentTimeMillis()) {
                _uiEvent.emit(AlarmUiEvent.ShowCard("Please choose a future time", ToastType.ERROR))
                return@launch
            }
            alarmScheduler.cancel(old)
            repository.deleteAlarm(old)
            val id = repository.insertAlarm(new)
            alarmScheduler.schedule(new.copy(id = id.toInt()))
            _uiEvent.emit(AlarmUiEvent.ShowCard("Alarm updated", ToastType.SUCCESS))
        }
    }
}

class AlarmViewModelFactory(
    private val repository: Repository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(repository, context) as T
    }
}
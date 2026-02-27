package com.example.presentation.alarms.viewmodel
import  com.example.presentation.utils.AlarmUiEvent
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.presentation.component.alert.alarm.AlarmScheduler
import com.example.data.model.entity.AlarmEntity
import com.example.presentation.utils.ToastType
import com.example.weather.R
import android.content.res.Configuration
import com.example.data.IRepository
import java.util.Locale
 import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AlarmViewModel(
    private val repository: IRepository,
    private val context: Context
) : ViewModel() {

    private val alarmScheduler = AlarmScheduler(context)

    private fun localizedString(resId: Int, vararg args: Any): String {
        val config = Configuration(context.resources.configuration).apply { setLocale(Locale.getDefault()) }
        return context.createConfigurationContext(config).getString(resId, *args)
    }

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
                _uiEvent.emit(AlarmUiEvent.ShowCard(localizedString(R.string.choose_future_time), ToastType.ERROR))
                return@launch
            }
            val id = repository.insertAlarm(alarm)
            alarmScheduler.schedule(alarm.copy(id = id.toInt()))
            _uiEvent.emit(AlarmUiEvent.ShowCard(localizedString(R.string.alarm_set_success), ToastType.SUCCESS))
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
                _uiEvent.emit(AlarmUiEvent.ShowCard(localizedString(R.string.choose_future_time), ToastType.ERROR))
                return@launch
            }
            alarmScheduler.cancel(old)
            repository.deleteAlarm(old)
            val id = repository.insertAlarm(new)
            alarmScheduler.schedule(new.copy(id = id.toInt()))
            _uiEvent.emit(AlarmUiEvent.ShowCard(localizedString(R.string.alarm_updated), ToastType.SUCCESS))
        }
    }
}

class AlarmViewModelFactory(
    private val repository: IRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(repository, context) as T
    }
}
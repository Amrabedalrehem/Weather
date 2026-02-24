package com.example.presentation.favorite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.alarm.AlarmScheduler
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.presentation.component.helper.ToastType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class FavUiEvent {
    data class ShowCard(
        val message: String,
        val type: ToastType = ToastType.INFO
    ) : FavUiEvent()
}

class FavoritesViewModel(
    val repository: Repository,
    context: Context
) : ViewModel() {

    private val alarmScheduler = AlarmScheduler(context)

    private val _uiEvent = MutableSharedFlow<FavUiEvent>()
    val uiEvent: SharedFlow<FavUiEvent> = _uiEvent

    private val pendingDeletion = MutableStateFlow<Set<Int>>(emptySet())

    val favourites: StateFlow<List<FavouriteLocationCache>> =
        repository.getAllFavourites()
            .combine(pendingDeletion) { list, pending ->
                list.filter { it.id !in pending }
            }
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    fun markForDeletion(location: FavouriteLocationCache) {
        pendingDeletion.value += location.id
    }

    fun confirmDelete(location: FavouriteLocationCache) {
        viewModelScope.launch {
            pendingDeletion.value -= location.id
            repository.delete(location)
        }
    }

    fun undoDelete(location: FavouriteLocationCache) {
        pendingDeletion.value -= location.id
    }

    val alarms: StateFlow<List<AlarmEntity>> = repository.getAllAlarms()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            if (alarm.timeInMillis <= System.currentTimeMillis()) {
                _uiEvent.emit(FavUiEvent.ShowCard("Please choose a future time", ToastType.ERROR))
                return@launch
            }
            val id = repository.insertAlarm(alarm)
            alarmScheduler.schedule(alarm.copy(id = id.toInt()))
            _uiEvent.emit(FavUiEvent.ShowCard("Alarm set for ${alarm.city}", ToastType.SUCCESS))
        }
    }

    fun disableAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm)
            repository.deleteAlarm(alarm)
        }
    }
}

class FavoritesViewModelFactory(
    private val repository: Repository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository, context) as T
    }
}
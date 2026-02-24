package com.example.presentation.component.helper

sealed class AlarmUiEvent {
    data class ShowCard(
        val message: String,
        val type: ToastType = ToastType.INFO
    ) : AlarmUiEvent()
}

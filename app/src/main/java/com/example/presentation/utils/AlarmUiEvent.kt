package com.example.presentation.utils

sealed class AlarmUiEvent {
    data class ShowCard(
        val message: String,
        val type: ToastType = ToastType.INFO
    ) : AlarmUiEvent()
}

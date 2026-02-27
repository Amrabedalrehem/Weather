package com.example.presentation.component.alert.alarm


import com.example.data.model.entity.AlarmEntity

interface IAlarmScheduler {
    fun schedule(alarm: AlarmEntity)
    fun cancel(alarm: AlarmEntity)
}
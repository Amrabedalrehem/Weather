package com.example.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val timeInMillis: Long,
    val type: String,
    val isActive: Boolean = true
)
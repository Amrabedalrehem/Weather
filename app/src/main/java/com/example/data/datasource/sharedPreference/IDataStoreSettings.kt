package com.example.data.datasource.sharedPreference
import kotlinx.coroutines.flow.Flow

interface IDataStoreSettings {
    val temperatureUnit: Flow<String>
    val locationType: Flow<String>
    val language: Flow<String>
    val windSpeedUnit: Flow<String>
    val theme: Flow<String>
    suspend fun saveTemperatureUnit(value: String)
    suspend fun saveLocationType(value: String)
    suspend fun saveLanguage(value: String)
    suspend fun saveWindSpeedUnit(value: String)
    suspend fun saveTheme(value: String)
}
package com.example.data.datasource.sharedPreference


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class DataStoreSettings(private val context: Context) {

    companion object {
         val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val LOCATION_TYPE    = stringPreferencesKey("location_type")
        val LANGUAGE         = stringPreferencesKey("language")
        val WIND_SPEED_UNIT  = stringPreferencesKey("wind_speed_unit")
        val THEME            = stringPreferencesKey("theme")
    }




    val temperatureUnit: Flow<String> = context.settingsDataStore.data
        .map { it[TEMPERATURE_UNIT] ?: "Celsius (°C)" }

    val locationType: Flow<String> = context.settingsDataStore.data
        .map { it[LOCATION_TYPE] ?: "Gps" }

    val language: Flow<String> = context.settingsDataStore.data
        .map { it[LANGUAGE] ?: "English" }

    val windSpeedUnit: Flow<String> = context.settingsDataStore.data
        .map { it[WIND_SPEED_UNIT] ?: "m/s" }

    val theme: Flow<String> = context.settingsDataStore.data
        .map { it[THEME] ?: "System" }

    suspend fun saveTemperatureUnit(value: String) {
        context.settingsDataStore.edit { it[TEMPERATURE_UNIT] = value }
    }

    suspend fun saveLocationType(value: String) {
        context.settingsDataStore.edit { it[LOCATION_TYPE] = value }
    }

    suspend fun saveLanguage(value: String) {
        context.settingsDataStore.edit { it[LANGUAGE] = value }
    }

    suspend fun saveWindSpeedUnit(value: String) {
        context.settingsDataStore.edit { it[WIND_SPEED_UNIT] = value }
    }

    suspend fun saveTheme(value: String) {
        context.settingsDataStore.edit { it[THEME] = value }
    }
}
package com.example.data.datasource.sharedPreference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
val Context.permissionDataStore: DataStore<Preferences> by preferencesDataStore(name = "permission_prefs")

class DataStorePermission(private val context: Context) {

    companion object {
        val PERMISSION_REQUESTED = booleanPreferencesKey("permission_requested")
        val LATITUDE       = doublePreferencesKey("latitude")
        val LONGITUDE      = doublePreferencesKey("longitude")
    }

    val wasPermissionRequested: Flow<Boolean> = context.permissionDataStore.data
        .map { it[PERMISSION_REQUESTED] ?: false }

    val latitude: Flow<Double> = context.permissionDataStore.data
        .map { it[LATITUDE] ?: 0.0 }

    val longitude: Flow<Double> = context.permissionDataStore.data
        .map { it[LONGITUDE] ?: 0.0 }

    suspend fun markPermissionRequested() {
        context.permissionDataStore.edit { it[PERMISSION_REQUESTED] = true }
    }

    suspend fun saveLocation(lat: Double, lon: Double) {
        context.permissionDataStore.edit {
            it[LATITUDE]  = lat
            it[LONGITUDE] = lon
        }
    }
}
package com.example.data.datasource.sharedPreference

import kotlinx.coroutines.flow.Flow

interface IDataStorePermission {
    val wasPermissionRequested: Flow<Boolean>
    val latitude: Flow<Double>
    val longitude: Flow<Double>
    suspend fun markPermissionRequested()
    suspend fun saveLocation(lat: Double, lon: Double)
}
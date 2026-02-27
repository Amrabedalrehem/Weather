package com.example.data.datasource.local


import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache
import kotlinx.coroutines.flow.Flow

interface IDataSourceLocal {
    fun getAllFavourites(): Flow<List<FavouriteLocationCache>>
    fun getFavouriteById(id: Int): Flow<FavouriteLocationCache?>
    suspend fun insert(location: FavouriteLocationCache)
    suspend fun delete(location: FavouriteLocationCache)
    fun getHomeWeather(): Flow<HomeWeatherCache?>
    suspend fun insertHomeWeather(cache: HomeWeatherCache)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
    suspend fun insertAlarm(alarm: AlarmEntity): Long
    suspend fun deleteAlarm(alarm: AlarmEntity)
    suspend fun updateAlarm(alarm: AlarmEntity)
    suspend fun toggleAlarm(id: Int, isActive: Boolean)
    suspend fun getAlarmById(id: Int): AlarmEntity?
}
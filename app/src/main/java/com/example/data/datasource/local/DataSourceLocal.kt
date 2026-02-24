package com.example.data.datasource.local

import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache

class DataSourceLocal(private val favouriteDao: FavouriteDao, private val homeWeatherDao: HomeWeatherDao
,    private val alarmDao: AlarmDao
) {

    fun getAllFavourites() = favouriteDao.getAllFavourites()

    fun getFavouriteById(id: Int)= favouriteDao.getFavouriteById(id)

    suspend fun insert(location: FavouriteLocationCache) = favouriteDao.insert(location)

    suspend fun delete(location: FavouriteLocationCache) = favouriteDao.delete(location)

    fun  getHomeWeather() = homeWeatherDao.getHomeWeather()

    suspend fun insertHomeWeather(cache: HomeWeatherCache) = homeWeatherDao.insertHomeWeather(cache)

    fun getAllAlarms() = alarmDao.getAllAlarms()
    suspend fun insertAlarm(alarm: AlarmEntity): Long = alarmDao.insertAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)
    suspend fun toggleAlarm(id: Int, isActive: Boolean) = alarmDao.toggleAlarm(id, isActive)
    suspend fun getAlarmById(id: Int) = alarmDao.getAlarmById(id)
}
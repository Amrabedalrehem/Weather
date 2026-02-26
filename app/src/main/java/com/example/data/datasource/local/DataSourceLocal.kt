package com.example.data.datasource.local

import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache

class DataSourceLocal(
    private val favouriteDao: FavouriteDao,
    private val homeWeatherDao: HomeWeatherDao,
    private val alarmDao: AlarmDao
) : IDataSourceLocal {

    override fun getAllFavourites() = favouriteDao.getAllFavourites()

    override fun getFavouriteById(id: Int) = favouriteDao.getFavouriteById(id)

    override suspend fun insert(location: FavouriteLocationCache) = favouriteDao.insert(location)

    override suspend fun delete(location: FavouriteLocationCache) = favouriteDao.delete(location)

    override fun getHomeWeather() = homeWeatherDao.getHomeWeather()

    override suspend fun insertHomeWeather(cache: HomeWeatherCache) = homeWeatherDao.insertHomeWeather(cache)

    override fun getAllAlarms() = alarmDao.getAllAlarms()

    override suspend fun insertAlarm(alarm: AlarmEntity): Long = alarmDao.insertAlarm(alarm)

    override suspend fun deleteAlarm(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)

    override suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)

    override suspend fun toggleAlarm(id: Int, isActive: Boolean) = alarmDao.toggleAlarm(id, isActive)

    override suspend fun getAlarmById(id: Int) = alarmDao.getAlarmById(id)
}
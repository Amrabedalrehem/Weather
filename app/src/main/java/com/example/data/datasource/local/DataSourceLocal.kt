package com.example.data.datasource.local

import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache

class DataSourceLocal(private val favouriteDao: FavouriteDao, private val homeWeatherDao: HomeWeatherDao) {

    fun getAllFavourites() = favouriteDao.getAllFavourites()

    fun getFavouriteById(id: Int)= favouriteDao.getFavouriteById(id)

    suspend fun insert(location: FavouriteLocationCache) = favouriteDao.insert(location)

    suspend fun delete(location: FavouriteLocationCache) = favouriteDao.delete(location)

    fun  getHomeWeather() = homeWeatherDao.getHomeWeather()

    suspend fun insertHomeWeather(cache: HomeWeatherCache) = homeWeatherDao.insertHomeWeather(cache)

}
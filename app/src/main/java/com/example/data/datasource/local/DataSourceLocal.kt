package com.example.data.datasource.local

import com.example.data.model.entity.FavouriteLocationCache

class DataSourceLocal(private val favouriteDao: FavouriteDao) {

    fun getAllFavourites() = favouriteDao.getAllFavourites()

    fun getFavouriteById(id: Int)= favouriteDao.getFavouriteById(id)

    suspend fun insert(location: FavouriteLocationCache) = favouriteDao.insert(location)

    suspend fun delete(location: FavouriteLocationCache) = favouriteDao.delete(location)
}
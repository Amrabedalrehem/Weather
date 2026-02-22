package com.example.data.datasource.local

import com.example.data.model.entity.FavouriteLocation

class DataSourceLocal(private val favouriteDao: FavouriteDao) {

    fun getAllFavourites() = favouriteDao.getAllFavourites()

    suspend fun insert(location: FavouriteLocation) = favouriteDao.insert(location)

    suspend fun delete(location: FavouriteLocation) = favouriteDao.delete(location)
}
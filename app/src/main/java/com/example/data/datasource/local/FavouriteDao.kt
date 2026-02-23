package com.example.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.entity.FavouriteLocationCache
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Flow<List<FavouriteLocationCache>>

    @Query("SELECT * FROM favourites WHERE id = :id")
    fun getFavouriteById(id: Int): Flow<FavouriteLocationCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: FavouriteLocationCache)

    @Delete
    suspend fun delete(location: FavouriteLocationCache)
}
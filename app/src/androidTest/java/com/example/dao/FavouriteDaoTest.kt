package com.example.dao


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.datasource.local.FavouriteDao
import com.example.data.dp.AppDatabase
import com.example.data.model.entity.FavouriteLocationCache
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouriteDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FavouriteDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.favouriteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllFavourites_emptyDatabase_returnsEmptyList() = runTest {
        // When getAllFavourites is called
        val result = dao.getAllFavourites().first()

        // Then the result should be an empty list
        assertEquals(emptyList<FavouriteLocationCache>(), result)
    }

    @Test
    fun getAllFavourites_afterInsert_returnsAllItems() = runTest {
        // Given data in the database
         val location1 = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        val location2 = FavouriteLocationCache(id = 2, city = "Riyadh", country = "SA", lat = 25.0, lon = 45.0)

        // When getAllFavourites is called
        dao.insert(location1)
        dao.insert(location2)
        val result = dao.getAllFavourites().first()

        // Then the result should contain both items
        assertEquals(2, result.size)
    }



    @Test
    fun getFavouriteById_existingId_returnsCorrectItem() = runTest {
        // Given data in the database
             val location= FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
             dao.insert(location)

        // When getFavouriteById is called with the correct id
        val result = dao.getFavouriteById(1).first()

        // Then the result should be the correct item
        assertEquals(location, result)
    }

    @Test
    fun insert_newLocation_savesCorrectly() = runTest {
        // Given data in the database
        val location= FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)

        // When insert is called with a new location
        dao.insert(location)
        val result = dao.getAllFavourites().first()

        // Then the result should contain the new item
        assertEquals(1, result.size)
        assertEquals(location, result[0])
    }

    @Test
    fun delete_existingLocation_removesFromDatabase() = runTest {
        // Given data in the database
        val location= FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        dao.insert(location)

        // When delete is called with the correct location
        dao.delete(location)
        val result = dao.getAllFavourites().first()

        // Then the result should not contain the deleted item
        assertEquals(0, result.size)
    }

}


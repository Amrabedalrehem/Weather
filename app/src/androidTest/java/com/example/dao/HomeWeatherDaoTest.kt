package com.example.dao
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.datasource.local.HomeWeatherDao
import com.example.data.dp.AppDatabase
import com.example.data.model.entity.HomeWeatherCache
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeWeatherDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: HomeWeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.homeWeatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getHomeWeather_emptyDatabase_returnsNull() = runTest {
        // When getHomeWeather is called
        val result = dao.getHomeWeather().first()

        // Then the result should be null
        assertNull(result)
    }

    @Test
    fun getHomeWeather_afterInsert_returnsCorrectData() = runTest {
        // Given data in the database
        val cache = HomeWeatherCache(id = 1)

        // When getHomeWeather is called
        dao.insertHomeWeather(cache)
        val result = dao.getHomeWeather().first()

        // Then the result should contain the correct data
        assertEquals(cache.id, result?.id)
    }

    @Test
    fun insertHomeWeather_newCache_savesCorrectly() = runTest {
        // Given data in the database
        val cache = HomeWeatherCache(id = 1)

        // When insertHomeWeather is called with a new cache
        dao.insertHomeWeather(cache)
        val result = dao.getHomeWeather().first()

        // Then the result should contain the new data
        assertEquals(1, result?.id)
    }

    @Test
    fun insertHomeWeather_insertTwice_replacesOldData() = runTest {
        // Given data in the database
        val oldCache = HomeWeatherCache(id = 1, lastUpdated = 1000L)
        val newCache = HomeWeatherCache(id = 1, lastUpdated = 2000L)

        // When insertHomeWeather is called twice with the same id
        dao.insertHomeWeather(oldCache)
        dao.insertHomeWeather(newCache)
        val result = dao.getHomeWeather().first()

        // Then the result should contain the new data
        assertEquals(2000L, result?.lastUpdated)
    }
}
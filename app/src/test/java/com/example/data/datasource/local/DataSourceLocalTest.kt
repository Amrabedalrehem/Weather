package com.example.data.datasource.local
import org.junit.Test
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before

class DataSourceLocalTest {

    private lateinit var favouriteDao: FavouriteDao
    private lateinit var homeWeatherDao: HomeWeatherDao
    private lateinit var alarmDao: AlarmDao
    private lateinit var dataSourceLocal: DataSourceLocal



    @Before
    fun setup() {
        favouriteDao = mockk()
        homeWeatherDao = mockk()
        alarmDao = mockk()
        dataSourceLocal = DataSourceLocal(favouriteDao, homeWeatherDao, alarmDao)
    }


    @Test
    fun getAllFavourites_returnsFavouritesList() = runTest {
        // Given data in the database
        val fakeList = listOf(
            FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0),
            FavouriteLocationCache(id = 2, city = "Alexandria", country = "EG", lat = 31.0, lon = 30.0)
        )
        every { favouriteDao.getAllFavourites() } returns flowOf(fakeList)

        // When getAllFavourites is called
        val result = dataSourceLocal.getAllFavourites().first()

        // Then the result should contain the correct data
        assertEquals(2, result.size)
        assertEquals("Cairo", result[0].city)
    }

    @Test
    fun getFavouriteById_existingId_returnsCorrectItem() = runTest {
        // Given data in the database
        val fakeLocation = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        every { favouriteDao.getFavouriteById(1) } returns flowOf(fakeLocation)

        // When getFavouriteById is called with the correct id
        val result = dataSourceLocal.getFavouriteById(1).first()

        // Then the result should contain the correct item
        assertEquals("Cairo", result?.city)
    }

    @Test
    fun insert_callsFavouriteDaoInsert() = runTest {
        // Given data in the database
        val location = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        coEvery { favouriteDao.insert(location) } returns Unit

        // When insert is called with a new location
        dataSourceLocal.insert(location)

        // Then the result should contain the new item
        coVerify { favouriteDao.insert(location) }
    }

    @Test
    fun delete_callsFavouriteDaoDelete() = runTest {
        // Given data in the database
        val location = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        coEvery { favouriteDao.delete(location) } returns Unit

        // When delete is called with the correct location
        dataSourceLocal.delete(location)

        // Then the result should not contain the deleted item
        coVerify { favouriteDao.delete(location) }
    }


    @Test
    fun getHomeWeather_returnsHomeWeatherCache() = runTest {
        // Given data in the database
        val fakeCache = HomeWeatherCache(id = 1, lastUpdated = 1000L)
        every { homeWeatherDao.getHomeWeather() } returns flowOf(fakeCache)

        // When getHomeWeather is called
        val result = dataSourceLocal.getHomeWeather().first()

        // Then the result should contain the correct data
        assertEquals(1000L, result?.lastUpdated)
    }

    @Test
    fun getHomeWeather_emptyDatabase_returnsNull() = runTest {
        // Given empty database
        every { homeWeatherDao.getHomeWeather() } returns flowOf(null)

        // When getHomeWeather is called
        val result = dataSourceLocal.getHomeWeather().first()

        // Then the result should be null
        assertNull(result)
    }

    @Test
    fun insertHomeWeather_callsHomeWeatherDaoInsert() = runTest {
        // Given data in the database
        val cache = HomeWeatherCache(id = 1, lastUpdated = 1000L)
        coEvery { homeWeatherDao.insertHomeWeather(cache) } returns Unit

        // When insertHomeWeather is called with a new cache
        dataSourceLocal.insertHomeWeather(cache)

        // Then the result should contain the new data
        coVerify { homeWeatherDao.insertHomeWeather(cache) }
    }


}
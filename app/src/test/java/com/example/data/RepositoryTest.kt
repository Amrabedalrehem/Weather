package com.example.data
import com.example.data.datasource.local.IDataSourceLocal
import com.example.data.datasource.remote.IDataSourceRemote
import com.example.data.datasource.sharedPreference.IDataStorePermission
import com.example.data.datasource.sharedPreference.IDataStoreSettings
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RepositoryTest {

    private lateinit var local: IDataSourceLocal
    private lateinit var remote: IDataSourceRemote
    private lateinit var settings: IDataStoreSettings
    private lateinit var permission: IDataStorePermission
    private lateinit var repository: Repository



    @Before
    fun setup() {
        local = mockk()
        remote = mockk()
        settings = mockk()
        permission = mockk()

        every { permission.wasPermissionRequested } returns flowOf(false)
        every { permission.latitude } returns flowOf(30.0)
        every { permission.longitude } returns flowOf(31.0)
        every { settings.temperatureUnit } returns flowOf("celsius")
        every { settings.windSpeedUnit } returns flowOf("meter/sec")
        every { settings.language } returns flowOf("en")
        every { settings.locationType } returns flowOf("gps")
        every { settings.theme } returns flowOf("light")

        repository = Repository(local, remote, settings, permission)
    }

    @Test
    fun getCurrentWeather_celsius_callsMetricUnits() = runTest {
        // Given data in the repository
        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
        }
        coEvery {
            remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "metric")
        } returns fakeResponse

        // When call getCurrentWeather with celsius
        val result = repository.getCurrentWeather()

        // Then the result should be successful
        assertTrue(result.isSuccessful)
        coVerify { remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "metric") }
    }

    @Test
    fun getCurrentWeather_fahrenheit_callsImperialUnits() = runTest {
        // Given data in the repository
        every { settings.temperatureUnit } returns flowOf("fahrenheit")
        repository = Repository(local, remote, settings, permission)

        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
        }
        coEvery {
            remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "imperial")
        } returns fakeResponse

        // When call getCurrentWeather with fahrenheit
        val result = repository.getCurrentWeather()

        // Then the result should be successful
        assertTrue(result.isSuccessful)
        coVerify { remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "imperial") }
    }

    @Test
    fun getCurrentWeather_kelvin_callsStandardUnits() = runTest {
        // Given data in the repository
        every { settings.temperatureUnit } returns flowOf("kelvin")
        repository = Repository(local, remote, settings, permission)

        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
        }
        coEvery {
            remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "standard")
        } returns fakeResponse

        // When call getCurrentWeather with kelvin
        repository.getCurrentWeather()

        // Then the result should be successful
        coVerify { remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "standard") }
    }

    @Test
    fun getCurrentWeather_withLatLon_callsCorrectParams() = runTest {
        // Given data in the repository
        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
        }
        coEvery {
            remote.getCurrentWeather(lat = 25.0, lon = 45.0, lang = "en", units = "metric")
        } returns fakeResponse

        // When call getCurrentWeather with lat and lon
        val result = repository.getCurrentWeather(lat = 25.0, lon = 45.0)

        // Then the result should be successful
        assertTrue(result.isSuccessful)
        coVerify { remote.getCurrentWeather(lat = 25.0, lon = 45.0, lang = "en", units = "metric") }
    }

    @Test
    fun getHourlyForecast_successResponse_returnsData() = runTest {
        // Given data in the repository
        val fakeResponse = mockk<Response<HourlyForecastResponse>> {
            every { isSuccessful } returns true
        }
        coEvery {
            remote.getHourlyForecast(city = "Cairo", units = "metric", lang = "en")
        } returns fakeResponse

        // When call getHourlyForecast
        val result = repository.getHourlyForecast("Cairo")

        // Then  the result should be successful
        assertTrue(result.isSuccessful)
        coVerify { remote.getHourlyForecast(city = "Cairo", units = "metric", lang = "en") }
    }


    @Test
    fun getFiveDayForecast_successResponse_returnsData() = runTest {
        // Given data in the repository
        val fakeResponse = mockk<Response<FiveDayForecastResponse>> {
            every { isSuccessful } returns true
        }
        coEvery {
            remote.getFiveDayForecast(
                city = "Cairo", lat = 30.0, lon = 31.0, units = "metric", lang = "en"
            )
        } returns fakeResponse

        // When call getFiveDayForecast
        val result = repository.getFiveDayForecast("Cairo")

        // Then the result should be successful
        assertTrue(result.isSuccessful)
        coVerify {
            remote.getFiveDayForecast(
                city = "Cairo", lat = 30.0, lon = 31.0, units = "metric", lang = "en"
            )
        }
    }


    @Test
    fun getAllFavourites_returnsList() = runTest {
        // Given data in the repository
        val fakeList = listOf(
            FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        )
        every { local.getAllFavourites() } returns flowOf(fakeList)

        // When call getAllFavourites
        val result = repository.getAllFavourites().first()

        // Then the result should contain the correct data
        assertEquals(1, result.size)
        assertEquals("Cairo", result[0].city)
    }

    @Test
    fun insert_callsLocalInsert() = runTest {
        // Given data in the repository
        val location = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        coEvery { local.insert(location) } returns Unit

        // When call insert with a new location
        repository.insert(location)

        // Then the result should contain the new item
        coVerify { local.insert(location) }
    }

    @Test
    fun delete_callsLocalDelete() = runTest {
        // Given data in the repository
        val location = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        coEvery { local.delete(location) } returns Unit

        // When call delete with the correct location
        repository.delete(location)

        // Then the result should not contain the deleted item
        coVerify { local.delete(location) }
    }


    @Test
    fun getHomeWeather_returnsCache() = runTest {
        // Given data in the repository
        val fakeCache = HomeWeatherCache(id = 1, lastUpdated = 1000L)
        every { local.getHomeWeather() } returns flowOf(fakeCache)

        // When call getHomeWeather
        val result = repository.getHomeWeather().first()

        // Then the result should contain the correct data
        assertEquals(1000L, result?.lastUpdated)
    }

    @Test
    fun insertAlarm_returnsGeneratedId() = runTest {
        // Given data in the repository
        val alarm = AlarmEntity(
            city = "Cairo", latitude = 30.0, longitude = 31.0,
            timeInMillis = 1000L, type = "notification"
        )
        coEvery { local.insertAlarm(alarm) } returns 1L

        // When call insertAlarm with a new alarm
        val result = repository.insertAlarm(alarm)

        // Then the result should contain the new alarm
        assertEquals(1L, result)
    }

    @Test
    fun toggleAlarm_callsLocalToggle() = runTest {
        // Given data in the repository
        coEvery { local.toggleAlarm(1, false) } returns Unit

        // When call toggleAlarm with the correct id
        repository.toggleAlarm(1, false)

        // Then the result should not contain the updated alarm
        coVerify { local.toggleAlarm(1, false) }
    }


    @Test
    fun clearSavedLocation_savesZeroLatLon() = runTest {
        // Given data in the repository
        coEvery { permission.saveLocation(0.0, 0.0) } returns Unit

        // When call clearSavedLocation
        repository.clearSavedLocation()

        // Then the result should not contain the updated alarm
        coVerify { permission.saveLocation(0.0, 0.0) }
    }

    @Test
    fun saveLocation_callsPermissionSaveLocation() = runTest {
        // Given data in the repository
        coEvery { permission.saveLocation(25.0, 45.0) } returns Unit

        // When call saveLocation with lat and lon
        repository.saveLocation(25.0, 45.0)

        // Then the result should not contain the updated alarm
        coVerify { permission.saveLocation(25.0, 45.0) }
    }
}
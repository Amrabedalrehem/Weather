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
import kotlinx.coroutines.flow.toList
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
    fun getCurrentWeather_celsius_emitsLoadingThenSuccess() = runTest {
        // Given
        val fakeDto = mockk<CurrentWeatherDto>()
        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
            every { body() } returns fakeDto
        }
        coEvery {
            remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "metric")
        } returns fakeResponse

        // When
        val results = repository.getCurrentWeather().toList()

        // Then
        assertTrue(results[0] is ApiResult.Loading)
        assertTrue(results[1] is ApiResult.Success)
        coVerify { remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "metric") }
    }

    @Test
    fun getCurrentWeather_fahrenheit_callsImperialUnits() = runTest {
        // Given
        every { settings.temperatureUnit } returns flowOf("fahrenheit")
        repository = Repository(local, remote, settings, permission)

        val fakeDto = mockk<CurrentWeatherDto>()
        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
            every { body() } returns fakeDto
        }
        coEvery {
            remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "imperial")
        } returns fakeResponse

        // When
        val results = repository.getCurrentWeather().toList()

        // Then
        assertTrue(results[1] is ApiResult.Success)
        coVerify { remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "imperial") }
    }

    @Test
    fun getCurrentWeather_kelvin_callsStandardUnits() = runTest {
        // Given
        every { settings.temperatureUnit } returns flowOf("kelvin")
        repository = Repository(local, remote, settings, permission)

        val fakeDto = mockk<CurrentWeatherDto>()
        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
            every { body() } returns fakeDto
        }
        coEvery {
            remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "standard")
        } returns fakeResponse

        // When
        repository.getCurrentWeather().toList()

        // Then
        coVerify { remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "standard") }
    }

    @Test
    fun getCurrentWeather_failureResponse_emitsError() = runTest {
        // Given
        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns false
            every { body() } returns null
            every { message() } returns "Not Found"
        }
        coEvery {
            remote.getCurrentWeather(lat = 30.0, lon = 31.0, lang = "en", units = "metric")
        } returns fakeResponse

        // When
        val results = repository.getCurrentWeather().toList()

        // Then
        assertTrue(results[0] is ApiResult.Loading)
        assertTrue(results[1] is ApiResult.Error)
        assertEquals("Not Found", (results[1] as ApiResult.Error).message)
    }


    @Test
    fun getCurrentWeather_withLatLon_emitsSuccess() = runTest {
        // Given
        val fakeDto = mockk<CurrentWeatherDto>()
        val fakeResponse = mockk<Response<CurrentWeatherDto>> {
            every { isSuccessful } returns true
            every { body() } returns fakeDto
        }
        coEvery {
            remote.getCurrentWeather(lat = 25.0, lon = 45.0, lang = "en", units = "metric")
        } returns fakeResponse

        // When
        val results = repository.getCurrentWeather(lat = 25.0, lon = 45.0).toList()

        // Then
        assertTrue(results[0] is ApiResult.Loading)
        assertTrue(results[1] is ApiResult.Success)
        coVerify { remote.getCurrentWeather(lat = 25.0, lon = 45.0, lang = "en", units = "metric") }
    }

    @Test
    fun getHourlyForecast_successResponse_emitsSuccess() = runTest {
        // Given
        val fakeDto = mockk<HourlyForecastResponse>()
        val fakeResponse = mockk<Response<HourlyForecastResponse>> {
            every { isSuccessful } returns true
            every { body() } returns fakeDto
        }
        coEvery {
            remote.getHourlyForecast(city = "Cairo", units = "metric", lang = "en")
        } returns fakeResponse

        // When
        val results = repository.getHourlyForecast("Cairo").toList()

        // Then
        assertTrue(results[0] is ApiResult.Loading)
        assertTrue(results[1] is ApiResult.Success)
        coVerify { remote.getHourlyForecast(city = "Cairo", units = "metric", lang = "en") }
    }

    @Test
    fun getHourlyForecast_failureResponse_emitsError() = runTest {
        // Given
        val fakeResponse = mockk<Response<HourlyForecastResponse>> {
            every { isSuccessful } returns false
            every { body() } returns null
            every { message() } returns "Server Error"
        }
        coEvery {
            remote.getHourlyForecast(city = "Cairo", units = "metric", lang = "en")
        } returns fakeResponse

        // When
        val results = repository.getHourlyForecast("Cairo").toList()

        // Then
        assertTrue(results[1] is ApiResult.Error)
        assertEquals("Server Error", (results[1] as ApiResult.Error).message)
    }


    @Test
    fun getFiveDayForecast_successResponse_emitsSuccess() = runTest {
        // Given
        val fakeDto = mockk<FiveDayForecastResponse>()
        val fakeResponse = mockk<Response<FiveDayForecastResponse>> {
            every { isSuccessful } returns true
            every { body() } returns fakeDto
        }
        coEvery {
            remote.getFiveDayForecast(
                city = "Cairo", lat = 30.0, lon = 31.0, units = "metric", lang = "en"
            )
        } returns fakeResponse

        // When
        val results = repository.getFiveDayForecast("Cairo").toList()

        // Then
        assertTrue(results[0] is ApiResult.Loading)
        assertTrue(results[1] is ApiResult.Success)
        coVerify {
            remote.getFiveDayForecast(
                city = "Cairo", lat = 30.0, lon = 31.0, units = "metric", lang = "en"
            )
        }
    }


    @Test
    fun getAllFavourites_returnsList() = runTest {
        // Given
        val fakeList = listOf(
            FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        )
        every { local.getAllFavourites() } returns flowOf(fakeList)

        // When
        val result = repository.getAllFavourites().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Cairo", result[0].city)
    }

    @Test
    fun insert_callsLocalInsert() = runTest {
        // Given
        val location = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        coEvery { local.insert(location) } returns Unit

        // When
        repository.insert(location)

        // Then
        coVerify { local.insert(location) }
    }

    @Test
    fun delete_callsLocalDelete() = runTest {
        // Given
        val location = FavouriteLocationCache(id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0)
        coEvery { local.delete(location) } returns Unit

        // When
        repository.delete(location)

        // Then
        coVerify { local.delete(location) }
    }

    @Test
    fun getHomeWeather_returnsCache() = runTest {
        // Given
        val fakeCache = HomeWeatherCache(id = 1, lastUpdated = 1000L)
        every { local.getHomeWeather() } returns flowOf(fakeCache)

        // When
        val result = repository.getHomeWeather().first()

        // Then
        assertEquals(1000L, result?.lastUpdated)
    }



    @Test
    fun insertAlarm_returnsGeneratedId() = runTest {
        // Given
        val alarm = AlarmEntity(
            city = "Cairo", latitude = 30.0, longitude = 31.0,
            timeInMillis = 1000L, type = "notification"
        )
        coEvery { local.insertAlarm(alarm) } returns 1L

        // When
        val result = repository.insertAlarm(alarm)

        // Then
        assertEquals(1L, result)
    }

    @Test
    fun toggleAlarm_callsLocalToggle() = runTest {
        // Given
        coEvery { local.toggleAlarm(1, false) } returns Unit

        // When
        repository.toggleAlarm(1, false)

        // Then
        coVerify { local.toggleAlarm(1, false) }
    }



    @Test
    fun clearSavedLocation_savesZeroLatLon() = runTest {
        // Given
        coEvery { permission.saveLocation(0.0, 0.0) } returns Unit

        // When
        repository.clearSavedLocation()

        // Then
        coVerify { permission.saveLocation(0.0, 0.0) }
    }

    @Test
    fun saveLocation_callsPermissionSaveLocation() = runTest {
        // Given
        coEvery { permission.saveLocation(25.0, 45.0) } returns Unit

        // When
        repository.saveLocation(25.0, 45.0)

        // Then
        coVerify { permission.saveLocation(25.0, 45.0) }
    }
}
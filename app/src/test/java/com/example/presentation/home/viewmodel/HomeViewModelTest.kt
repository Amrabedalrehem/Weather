package com.example.presentation.home.viewmodel

import com.example.data.ApiResult
import com.example.data.IRepository
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.HomeWeatherCache
import com.example.presentation.utils.CheckNetwork
import com.example.presentation.utils.UiState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: IRepository
    private lateinit var networkObserver: CheckNetwork
    private lateinit var viewModel: HomeViewModel


    private val fakeCurrentWeather = mockk<CurrentWeatherDto>(relaxed = true) {
        every { name } returns "Cairo"
    }
    private val fakeHourlyForecast = mockk<HourlyForecastResponse>(relaxed = true)
    private val fakeFiveDayForecast = mockk<FiveDayForecastResponse>(relaxed = true)


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk(relaxed = true)
        networkObserver = mockk(relaxed = true)

        // Default device is ONLINE
        every { networkObserver.isConnected } returns MutableStateFlow(true)

        // Default no cached data
        every { repository.getHomeWeather() } returns flowOf(null)

        // Default settings flows (needed by init combine)
        every { repository.language } returns flowOf("en")
        every { repository.temperatureUnit } returns flowOf("celsius")
        every { repository.latitude } returns flowOf(30.0)
        every { repository.longitude } returns flowOf(31.0)

        // Default all API calls succeed
        every { repository.getCurrentWeather() } returns flowOf(
            ApiResult.Loading,
            ApiResult.Success(fakeCurrentWeather)
        )
        // Default all API calls succeed
        every { repository.getHourlyForecast(any()) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Success(fakeHourlyForecast)
        )
        // Default all API calls succeed
        every { repository.getFiveDayForecast(any()) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Success(fakeFiveDayForecast)
        )
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher after the test
        Dispatchers.resetMain()
    }

    // Helper to build ViewModel after customising mocks
    private fun buildViewModel() {
        viewModel = HomeViewModel(repository, networkObserver)
    }

    @Test
    fun getInfoWeatherOnline_CurrentWeather_emitsSuccess() = runTest {
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  currentWeather emits Success
        assertTrue(viewModel.currentWeather.value is UiState.Success)
        assertEquals(fakeCurrentWeather, (viewModel.currentWeather.value as UiState.Success).data)
    }

    @Test
    fun getInfoWeatherOnline_HourlyForecast_EmitsSuccess() = runTest {
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  hourlyForecast emits Success
        assertTrue(viewModel.hourlyForecast.value is UiState.Success)
        assertEquals(fakeHourlyForecast, (viewModel.hourlyForecast.value as UiState.Success).data)
    }

    @Test
    fun getInfoWeatherOnline_FiveDayForecast_EmitsSuccess() = runTest {
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  fiveDayForecast emits Success
        assertTrue(viewModel.fiveDayForecast.value is UiState.Success)
        assertEquals(fakeFiveDayForecast, (viewModel.fiveDayForecast.value as UiState.Success).data)
    }

    @Test
    fun getInfoWeatherOnline_CurrentWeatherForecasts_UsesCityNameFrom() = runTest {
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  hourlyForecast emits Success
        coVerify { repository.getHourlyForecast("Cairo") }
        coVerify { repository.getFiveDayForecast("Cairo") }
    }

    @Test
    fun getInfoWeatherOnline_CurrentWeatherIsNotSuccess_FallsBackToCairoWhen() = runTest {
        // Mock the repository's behavior
        every { repository.getCurrentWeather() } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error("timeout")
        )
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        // Should still call forecasts with default "Cairo"
        coVerify { repository.getHourlyForecast("Cairo") }
        coVerify { repository.getFiveDayForecast("Cairo") }
    }


    @Test
    fun getInfoWeather_CurrentWeatherAPIError_emitsErrorState() = runTest {
        // Mock the repository's behavior
        every { repository.getCurrentWeather() } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error("Network timeout")
        )
        // //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  currentWeather emits Error
        val state = viewModel.currentWeather.value
        assertTrue(state is UiState.Error)
        assertEquals("Network timeout", (state as UiState.Error).message)
    }

    @Test
    fun getInfoWeather_HourlyForecastAPIError_EmitsErrorState() = runTest {
        // Mock the repository's behavior
        every { repository.getHourlyForecast(any()) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error("Server Error")
        )
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  hourlyForecast emits Error
        val state = viewModel.hourlyForecast.value
        assertTrue(state is UiState.Error)
        assertEquals("Server Error", (state as UiState.Error).message)
    }

    @Test
    fun getInfoWeather_FiveDayForecastAPIError_EmitsErrorState() = runTest {
        // Mock the repository's behavior
        every { repository.getFiveDayForecast(any()) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error("Not Found")
        )
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
//then  fiveDayForecast emits Error
        val state = viewModel.fiveDayForecast.value
        assertTrue(state is UiState.Error)
        assertEquals("Not Found", (state as UiState.Error).message)
    }


    @Test
    fun getInfoWeatherOfflineWithCache_loadsAllThreeStatesFromCache() = runTest {
        // Mock the repository's behavior
        every { networkObserver.isConnected } returns MutableStateFlow(false)
        val cache = HomeWeatherCache(
            currentWeather = fakeCurrentWeather,
            hourlyForecast = fakeHourlyForecast,
            fiveDayForecast = fakeFiveDayForecast
        )
        every { repository.getHomeWeather() } returns flowOf(cache)
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  all three states emit Success
        assertTrue(viewModel.currentWeather.value is UiState.Success)
        assertTrue(viewModel.hourlyForecast.value is UiState.Success)
        assertTrue(viewModel.fiveDayForecast.value is UiState.Success)
    }

    @Test
    fun getInfoWeather_OfflineWithCache_DoesNOTCallRemoteAPI() = runTest {
        // Mock the repository's behavior
        every { networkObserver.isConnected } returns MutableStateFlow(false)

        val cache = HomeWeatherCache(
            currentWeather = fakeCurrentWeather,
            hourlyForecast = fakeHourlyForecast,
            fiveDayForecast = fakeFiveDayForecast
        )
        every { repository.getHomeWeather() } returns flowOf(cache)
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  no API calls should be made
        coVerify(exactly = 0) { repository.getCurrentWeather() }
        coVerify(exactly = 0) { repository.getHourlyForecast(any()) }
        coVerify(exactly = 0) { repository.getFiveDayForecast(any()) }
    }

    @Test
    fun getInfoWeatherOfflineNoCacheAllThree_StatesEmitError() = runTest {
        // Mock the repository's behavior
        every { networkObserver.isConnected } returns MutableStateFlow(false)
        every { repository.getHomeWeather() } returns flowOf(null)
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  all three states emit Error
        val expectedMessage = "No internet & No cached data"
        assertTrue(viewModel.currentWeather.value is UiState.Error)
        assertTrue(viewModel.hourlyForecast.value is UiState.Error)
        assertTrue(viewModel.fiveDayForecast.value is UiState.Error)
        assertEquals(expectedMessage, (viewModel.currentWeather.value as UiState.Error).message)
        assertEquals(expectedMessage, (viewModel.hourlyForecast.value as UiState.Error).message)
        assertEquals(expectedMessage, (viewModel.fiveDayForecast.value as UiState.Error).message)
    }


    @Test
    fun cacheWeatherData_DoesNOTInsertCache_WhenCurrentWeatherIsError() = runTest {
        // Mock the repository's behavior
        every { repository.getCurrentWeather() } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error("error")
        )
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  no API calls should be made
        coVerify(exactly = 0) { repository.insertHomeWeather(any()) }
    }

    @Test
    fun cacheWeatherData_DoesNOTInsertCache_WhenHourlyForecastIsError() = runTest {
        // Mock the repository's behavior
        every { repository.getHourlyForecast(any()) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error("error")
        )
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  no API calls should be made
        coVerify(exactly = 0) { repository.insertHomeWeather(any()) }
    }

    @Test
    fun cacheWeatherData_DoesNOTInsertCache_WhenFiveDayForecastIsError() = runTest {
        // Mock the repository's behavior
        every { repository.getFiveDayForecast(any()) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error("error")
        )
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
//then  no API calls should be made
        coVerify(exactly = 0) { repository.insertHomeWeather(any()) }
    }



    @Test
    fun isConnected_ReflectsNetworkObserverValue_whenDisconnected() = runTest {
        // Mock the repository's behavior
        every { networkObserver.isConnected } returns MutableStateFlow(false)
        every { repository.getHomeWeather() } returns flowOf(null)
        //when  call getInfoWeather()
        buildViewModel()
        advanceUntilIdle()
        //then  isConnected emits false
        assertTrue(!viewModel.isConnected.value)
    }

    @Test
    fun `init - re-fetches weather when language changes`() = runTest {
        // Mock the repository's behavior
        val langFlow = MutableStateFlow("en")
        every { repository.language } returns langFlow
        //when  call getInfoWeather() and Simulate language change
        buildViewModel()
        advanceUntilIdle()
        langFlow.value = "ar"
        advanceUntilIdle()

        // getCurrentWeather should be called at least twice (initial + after change)
        coVerify(atLeast = 2) { repository.getCurrentWeather() }
    }

    @Test
    fun `init - re-fetches weather when temperatureUnit changes`() = runTest {
        // Mock the repository's behavior
        val unitFlow = MutableStateFlow("celsius")
        every { repository.temperatureUnit } returns unitFlow
        //when  call getInfoWeather() and temperatureUnit  change
        buildViewModel()
        advanceUntilIdle()

        unitFlow.value = "fahrenheit"
        advanceUntilIdle()
        // getCurrentWeather should be called at least twice (initial + after change)
        coVerify(atLeast = 2) { repository.getCurrentWeather() }
    }

    @Test
    fun `init - re-fetches weather on network reconnection`() = runTest {
        // Mock the repository's behavior
        val connectedFlow = MutableStateFlow(false)
        every { networkObserver.isConnected } returns connectedFlow
        every { repository.getHomeWeather() } returns flowOf(null)
        //when  call getInfoWeather()  Device comes back online
        buildViewModel()
        advanceUntilIdle()

        connectedFlow.value = true
        advanceUntilIdle()
        // getCurrentWeather should be called at least once (initial + after reconnection)
        coVerify(atLeast = 1) { repository.getCurrentWeather() }
    }
}
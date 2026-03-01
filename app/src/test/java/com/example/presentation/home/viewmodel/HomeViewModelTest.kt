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
}
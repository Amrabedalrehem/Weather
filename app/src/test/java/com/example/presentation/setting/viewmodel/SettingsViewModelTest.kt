package com.example.presentation.setting.viewmodel

import com.example.data.IRepository
import com.example.presentation.utils.CheckNetwork
import com.google.android.gms.location.FusedLocationProviderClient
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: IRepository
    private lateinit var networkObserver: CheckNetwork
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {

         Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        networkObserver = mockk(relaxed = true)
        fusedLocationClient = mockk(relaxed = true)
        every { repository.temperatureUnit } returns flowOf("celsius")
        every { repository.windSpeedUnit } returns flowOf("ms")
        every { repository.language } returns flowOf("en")
        every { repository.locationType } returns flowOf("gps")
        every { repository.theme } returns flowOf("light")
        every { networkObserver.isConnected } returns MutableStateFlow(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() {
        // When → ViewModel is created
        viewModel =
            SettingsViewModel(repository, networkObserver, fusedLocationClient)
    }

    @Test
    fun `isConnected - true when network is connected`() = runTest {
        // given  network is connected
        every { networkObserver.isConnected } returns MutableStateFlow(true)

        // when ViewModel is created
        buildViewModel()
        advanceUntilIdle()

        // then isConnected is true
        assertTrue(viewModel.isConnected.value)
    }

    @Test
    fun `isConnected - initial value is true`() = runTest {

        // given  ViewModel is created
        buildViewModel()

        // when  checking initial state without advancing

        // Then  isConnected is true
        assertTrue(viewModel.isConnected.value)
    }

    @Test
    fun `saveTemperature - calls repository saveTemperatureUnit with correct value`() =
        runTest {

            // given ViewModel is created
            buildViewModel()

            // when saveTemperature is called
            viewModel.saveTemperature("fahrenheit")
            advanceUntilIdle()

            // Then repository saveTemperatureUnit is called with fahrenheit
            coVerify { repository.saveTemperatureUnit("fahrenheit") }
        }

    @Test
    fun `saveTemperature - celsius calls repository with celsius`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveTemperature is called
            viewModel.saveTemperature("celsius")
            advanceUntilIdle()

            // Then repository saveTemperatureUnit is called with celsius
            coVerify { repository.saveTemperatureUnit("celsius") }
        }

    @Test
    fun `saveTemperature - kelvin calls repository with kelvin`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveTemperature is called
            viewModel.saveTemperature("kelvin")
            advanceUntilIdle()

            // Then repository saveTemperatureUnit is called with kelvin
            coVerify { repository.saveTemperatureUnit("kelvin") }
        }

    @Test
    fun `saveWindSpeed - calls repository saveWindSpeedUnit with correct value`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveWindSpeed is called
            viewModel.saveWindSpeed("kmh")
            advanceUntilIdle()

            // Then repository saveWindSpeedUnit is called with kmh
            coVerify { repository.saveWindSpeedUnit("kmh") }
        }

    @Test
    fun `saveWindSpeed - ms calls repository with ms`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveWindSpeed is called
            viewModel.saveWindSpeed("ms")
            advanceUntilIdle()

            // Then repository saveWindSpeedUnit is called with ms
            coVerify { repository.saveWindSpeedUnit("ms") }
        }

    @Test
    fun `saveLanguageAndWait - calls repository saveLanguage with correct value`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveLanguageAndWait is called
            viewModel.saveLanguageAndWait("ar")
            advanceUntilIdle()

            // Then repository saveLanguage is called with ar
            coVerify { repository.saveLanguage("ar") }
        }

    @Test
    fun `saveLanguageAndWait - english calls repository with en`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveLanguageAndWait is called
            viewModel.saveLanguageAndWait("en")
            advanceUntilIdle()

            // Then repository saveLanguage is called with en
            coVerify { repository.saveLanguage("en") }
        }

    @Test
    fun `saveLocationType - calls repository saveLocationType with gps`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveLocationType is called
            viewModel.saveLocationType("gps")
            advanceUntilIdle()

            // Then repository saveLocationType is called with gps
            coVerify { repository.saveLocationType("gps") }
        }

    @Test
    fun `saveLocationType - calls repository saveLocationType with map`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveLocationType is called

            viewModel.saveLocationType("map")
            advanceUntilIdle()

            // Then repository saveLocationType is called with map
            coVerify { repository.saveLocationType("map") }
        }

    @Test
    fun `saveTheme - calls repository saveTheme with dark`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveTheme is called
            viewModel.saveTheme("dark")
            advanceUntilIdle()

            // Then repository saveTheme is called with dark
            coVerify { repository.saveTheme("dark") }
        }

    @Test
    fun `saveTheme - calls repository saveTheme with light`() =
        runTest {

            // Given ViewModel is created
            buildViewModel()

            // When saveTheme is called
            viewModel.saveTheme("light")
            advanceUntilIdle()

            // Then repository saveTheme is called with light
            coVerify { repository.saveTheme("light") }
        }
}
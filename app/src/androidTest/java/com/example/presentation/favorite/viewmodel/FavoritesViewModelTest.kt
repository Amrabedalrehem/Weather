package com.example.presentation.favorite.viewmodel

import android.content.Context
import com.example.data.IRepository
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.presentation.component.alert.alarm.IAlarmScheduler
import com.example.presentation.utils.ToastType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class FavoritesViewModelTest {

    private lateinit var repository: IRepository
    private lateinit var alarmScheduler: IAlarmScheduler
    private lateinit var context: Context
    private lateinit var viewModel: FavoritesViewModel

    private val testDispatcher = StandardTestDispatcher()

    // ========================
    // Setup & Teardown
    // ========================

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk()
        alarmScheduler = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { repository.getAllFavourites() } returns flowOf(emptyList())
        every { repository.getAllAlarms() } returns flowOf(emptyList())

        viewModel = FavoritesViewModel(repository, context, alarmScheduler)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========================
    // markForDeletion()
    // ========================

    @Test
    fun markForDeletion_addsIdToPendingDeletion() = runTest {
        // Given
        val location = FavouriteLocationCache(
            id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0
        )
        every { repository.getAllFavourites() } returns flowOf(listOf(location))
        viewModel = FavoritesViewModel(repository, context, alarmScheduler)

        // When
        viewModel.markForDeletion(location)
        advanceUntilIdle()

        // Then
        val result = viewModel.favourites.value
        assertTrue(result.none { it.id == 1 })
    }

    // ========================
    // confirmDelete()
    // ========================

    @Test
    fun confirmDelete_callsRepositoryDelete() = runTest {
        // Given
        val location = FavouriteLocationCache(
            id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0
        )
        coEvery { repository.delete(location) } returns Unit

        // When
        viewModel.confirmDelete(location)
        advanceUntilIdle()

        // Then
        coVerify { repository.delete(location) }
    }

    // ========================
    // undoDelete()
    // ========================

    @Test
    fun undoDelete_removesIdFromPendingDeletion() = runTest {
        // Given
        val location = FavouriteLocationCache(
            id = 1, city = "Cairo", country = "EG", lat = 30.0, lon = 31.0
        )
        every { repository.getAllFavourites() } returns flowOf(listOf(location))
        viewModel = FavoritesViewModel(repository, context, alarmScheduler)

        viewModel.markForDeletion(location)
        advanceUntilIdle()

        // When
        viewModel.undoDelete(location)
        advanceUntilIdle()

        // Then
        val result = viewModel.favourites.value
        assertTrue(result.any { it.id == 1 })
    }

    // ========================
    // addAlarm()
    // ========================

    @Test
    fun addAlarm_pastTime_emitsErrorEvent() = runTest {
        // Given
        val alarm = AlarmEntity(
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = 1000L, // وقت في الماضي
            type = "notification"
        )
        every { context.resources } returns mockk(relaxed = true)
        every { context.resources.configuration } returns mockk(relaxed = true)
        every { context.createConfigurationContext(any()) } returns mockk(relaxed = true) {
            every { getString(any()) } returns "Choose future time"
            every { getString(any(), any()) } returns "Choose future time"
        }

        val events = mutableListOf<FavUiEvent>()
        val job = kotlinx.coroutines.launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        // When
        viewModel.addAlarm(alarm)
        advanceUntilIdle()

        // Then
        assertTrue(events.any { it is FavUiEvent.ShowCard && (it as FavUiEvent.ShowCard).type == ToastType.ERROR })

        job.cancel()
    }

    @Test
    fun addAlarm_futureTime_schedulesAlarmAndEmitsSuccess() = runTest {
        // Given
        val alarm = AlarmEntity(
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = System.currentTimeMillis() + 100000L, // وقت في المستقبل
            type = "notification"
        )
        coEvery { repository.insertAlarm(alarm) } returns 1L
        every { context.resources } returns mockk(relaxed = true)
        every { context.resources.configuration } returns mockk(relaxed = true)
        every { context.createConfigurationContext(any()) } returns mockk(relaxed = true) {
            every { getString(any()) } returns "Alarm set"
            every { getString(any(), any()) } returns "Alarm set for Cairo"
        }

        val events = mutableListOf<FavUiEvent>()
        val job = kotlinx.coroutines.launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        // When
        viewModel.addAlarm(alarm)
        advanceUntilIdle()

        // Then
        coVerify { repository.insertAlarm(alarm) }
        verify { alarmScheduler.schedule(any()) }
        assertTrue(events.any { it is FavUiEvent.ShowCard && (it as FavUiEvent.ShowCard).type == ToastType.SUCCESS })

        job.cancel()
    }

    // ========================
    // disableAlarm()
    // ========================

    @Test
    fun disableAlarm_cancelsAndDeletesAlarm() = runTest {
        // Given
        val alarm = AlarmEntity(
            id = 1,
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = 1000L,
            type = "notification"
        )
        coEvery { repository.deleteAlarm(alarm) } returns Unit

        // When
        viewModel.disableAlarm(alarm)
        advanceUntilIdle()

        // Then
        verify { alarmScheduler.cancel(alarm) }
        coVerify { repository.deleteAlarm(alarm) }
    }
}
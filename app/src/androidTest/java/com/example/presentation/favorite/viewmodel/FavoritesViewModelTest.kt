package com.example.presentation.favorite.viewmodel
 
import android.content.Context
import com.example.data.IRepository
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.presentation.component.alert.alarm.IAlarmScheduler
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
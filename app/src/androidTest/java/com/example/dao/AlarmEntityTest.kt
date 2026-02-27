package com.example.dao
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.datasource.local.AlarmDao
import com.example.data.dp.AppDatabase
import com.example.data.model.entity.AlarmEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlarmDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AlarmDao


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.alarmDao()
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun insertAlarm_newAlarm_savesCorrectly() = runTest {
        // Given data in the database
        val alarm = AlarmEntity(city = "Cairo", latitude = 30.0, longitude = 31.0, timeInMillis = 1000L,
            type = "notification"
        )

        // When insertAlarm is called with a new alarm
        val id = dao.insertAlarm(alarm)
        val result = dao.getAllAlarms().first()

        // Then the result should contain the new alarm
        assertTrue(id > 0)
        assertEquals(1, result.size)
        assertEquals("Cairo", result[0].city)
    }

    @Test
    fun deleteAlarm_existingAlarm_removesFromDatabase() = runTest {
        // Given data in the database
        val alarm = AlarmEntity(
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = 1000L,
            type = "notification"
        )
        val id = dao.insertAlarm(alarm)
        val inserted = dao.getAlarmById(id.toInt())!!

        // When deleteAlarm is called with the correct alarm
        dao.deleteAlarm(inserted)
        val result = dao.getAllAlarms().first()

        // Then the result should not contain the deleted alarm
        assertEquals(0, result.size)
    }


    @Test
    fun updateAlarm_existingAlarm_updatesCorrectly() = runTest {
        // Given data in the database
        val alarm = AlarmEntity(
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = 1000L,
            type = "notification"
        )
        val id = dao.insertAlarm(alarm)
        val inserted = dao.getAlarmById(id.toInt())!!
        val updated = inserted.copy(city = "Alexandria", timeInMillis = 2000L)

        // When updateAlarm is called with the correct alarm
        dao.updateAlarm(updated)
        val result = dao.getAlarmById(id.toInt())

        // Then the result should contain the updated alarm
        assertEquals("Alexandria", result?.city)
        assertEquals(2000L, result?.timeInMillis)
    }



    @Test
    fun getAllAlarms_emptyDatabase_returnsEmptyList() = runTest {
        // When getAllAlarms is called
        val result = dao.getAllAlarms().first()

        // Then the result should be an empty list
        assertEquals(emptyList<AlarmEntity>(), result)
    }

    @Test
    fun getAllAlarms_multipleAlarms_returnsSortedByTime() = runTest {
        // Given data in the database
        val alarm1 = AlarmEntity(
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = 3000L,
            type = "notification"
        )
        val alarm2 = AlarmEntity(
            city = "Alexandria",
            latitude = 31.0,
            longitude = 30.0,
            timeInMillis = 1000L,
            type = "notification"
        )

        // When getAllAlarms is called
        dao.insertAlarm(alarm1)
        dao.insertAlarm(alarm2)
        val result = dao.getAllAlarms().first()

        // Then the result should contain the correct alarms
        assertEquals(2, result.size)
        assertEquals(1000L, result[0].timeInMillis) // Alexandria first (sorted ASC)
        assertEquals(3000L, result[1].timeInMillis)
    }



    @Test
    fun getAlarmById_existingId_returnsCorrectAlarm() = runTest {
        // Given data in the database
        val alarm = AlarmEntity(
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = 1000L,
            type = "notification"
        )
        val id = dao.insertAlarm(alarm)

        // When getAlarmById is called with the correct id
        val result = dao.getAlarmById(id.toInt())

        // Then the result should contain the correct alarm
        assertEquals("Cairo", result?.city)
    }

    @Test
    fun getAlarmById_nonExistingId_returnsNull() = runTest {
        // When
        val result = dao.getAlarmById(999)

        // Then
        assertNull(result)
    }

    @Test
    fun toggleAlarm_activeAlarm_deactivatesCorrectly() = runTest {
        // Given data in the database
        val alarm = AlarmEntity(
            city = "Cairo",
            latitude = 30.0,
            longitude = 31.0,
            timeInMillis = 1000L,
            type = "notification",
            isActive = true
        )
        val id = dao.insertAlarm(alarm)

        // When toggleAlarm is called with the correct id
        dao.toggleAlarm(id.toInt(), false)
        val result = dao.getAlarmById(id.toInt())

        // Then the result should contain the updated alarm
        assertEquals(false, result?.isActive)
    }
}
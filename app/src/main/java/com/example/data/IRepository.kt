package com.example.data


import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IRepository {
    val wasPermissionRequested: Flow<Boolean>
    val temperatureUnit: Flow<String>
    val windSpeedUnit: Flow<String>
    val language: Flow<String>
    val locationType: Flow<String>
    val theme: Flow<String>
    val latitude: Flow<Double>
    val longitude: Flow<Double>

    suspend fun markPermissionRequested()
    suspend fun saveLocation(lat: Double, lon: Double)
    suspend fun saveTemperatureUnit(value: String)
    suspend fun saveWindSpeedUnit(value: String)
    suspend fun saveLanguage(value: String)
    suspend fun saveLocationType(value: String)
    suspend fun saveTheme(value: String)

    suspend fun getCurrentWeather(): Response<CurrentWeatherDto>
    suspend fun getCurrentWeather(lat: Double, lon: Double): Response<CurrentWeatherDto>
    suspend fun getHourlyForecast(city: String): Response<HourlyForecastResponse>
    suspend fun getFiveDayForecast(city: String): Response<FiveDayForecastResponse>

    fun getAllFavourites(): Flow<List<FavouriteLocationCache>>
    fun getFavouriteById(id: Int): Flow<FavouriteLocationCache?>
    suspend fun insert(location: FavouriteLocationCache)
    suspend fun delete(location: FavouriteLocationCache)
    fun getHomeWeather(): Flow<HomeWeatherCache?>
    suspend fun insertHomeWeather(cache: HomeWeatherCache)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
    suspend fun insertAlarm(alarm: AlarmEntity): Long
    suspend fun deleteAlarm(alarm: AlarmEntity)
    suspend fun updateAlarm(alarm: AlarmEntity)
    suspend fun toggleAlarm(id: Int, isActive: Boolean)
    suspend fun getAlarmById(id: Int): AlarmEntity?
    suspend fun clearSavedLocation()
}
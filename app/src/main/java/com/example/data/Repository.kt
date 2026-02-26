package com.example.data

import com.example.data.datasource.local.DataSourceLocal
import com.example.data.datasource.remote.DataSourceRemote
import com.example.data.datasource.sharedPreference.DataStorePermission
import com.example.data.datasource.sharedPreference.DataStoreSettings
import com.example.data.model.dto.CurrentWeatherDto
import com.example.data.model.dto.FiveDayForecastResponse
import com.example.data.model.dto.HourlyForecastResponse
import com.example.data.model.entity.AlarmEntity
import com.example.data.model.entity.FavouriteLocationCache
import com.example.data.model.entity.HomeWeatherCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Response

class Repository(
    private val local: DataSourceLocal,
    private val remote: DataSourceRemote,
    private val settings: DataStoreSettings,
    private val permission: DataStorePermission
) {

    val wasPermissionRequested: Flow<Boolean> = permission.wasPermissionRequested
    suspend fun markPermissionRequested() = permission.markPermissionRequested()

    val temperatureUnit: Flow<String> = settings.temperatureUnit
    val windSpeedUnit: Flow<String> = settings.windSpeedUnit
    val language: Flow<String> = settings.language
    val locationType: Flow<String> = settings.locationType
    val theme: Flow<String> = settings.theme
    val latitude: Flow<Double> = permission.latitude
    val longitude: Flow<Double> = permission.longitude
    suspend fun saveLocation(lat: Double, lon: Double) = permission.saveLocation(lat, lon)
     suspend fun saveTemperatureUnit(value: String) = settings.saveTemperatureUnit(value)
    suspend fun saveWindSpeedUnit(value: String) = settings.saveWindSpeedUnit(value)
    suspend fun saveLanguage(value: String) = settings.saveLanguage(value)
    suspend fun saveLocationType(value: String) = settings.saveLocationType(value)
    suspend fun saveTheme(value: String) = settings.saveTheme(value)

    private fun mapUnits(unit: String): String = when (unit) {
        "celsius"    -> "metric"
        "fahrenheit" -> "imperial"
        else         -> "standard"
    }

    private fun mapLanguage(lang: String): String = when (lang) {
        "en"  -> "en"
        "ar"  -> "ar"
        else  -> "en"
    }

    suspend fun getCurrentWeather(): Response<CurrentWeatherDto> {
        val lat   = permission.latitude.first()
        val lon   = permission.longitude.first()
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getCurrentWeather(lat = lat, lon = lon, lang = lang, units = units)
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): Response<CurrentWeatherDto> {
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getCurrentWeather(lat = lat, lon = lon, lang = lang, units = units)
    }

    suspend fun getHourlyForecast(city: String): Response<HourlyForecastResponse> {
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getHourlyForecast(city = city, units = units, lang = lang)
    }

    suspend fun getFiveDayForecast(city: String): Response<FiveDayForecastResponse> {
        val lat   = permission.latitude.first()
        val lon   = permission.longitude.first()
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getFiveDayForecast(
            city = city, lat = lat, lon = lon, units = units, lang = lang
        )
    }
    private fun convertWindSpeed(speed: Double, windUnit: String): Double {
        return when (windUnit) {
            "mph" -> speed * 2.23694
            else  -> speed
        }
    }

    fun getAllFavourites() = local.getAllFavourites()
    fun getFavouriteById(id: Int) = local.getFavouriteById(id)
    suspend fun insert(location: FavouriteLocationCache) = local.insert(location)
    suspend fun delete(location: FavouriteLocationCache) = local.delete(location)
    fun getHomeWeather() = local.getHomeWeather()
    suspend fun insertHomeWeather(cache: HomeWeatherCache) = local.insertHomeWeather(cache)
    fun getAllAlarms() = local.getAllAlarms()
    suspend fun insertAlarm(alarm: AlarmEntity): Long = local.insertAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmEntity) = local.deleteAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = local.updateAlarm(alarm)
    suspend fun toggleAlarm(id: Int, isActive: Boolean) = local.toggleAlarm(id, isActive)
    suspend fun getAlarmById(id: Int) = local.getAlarmById(id)

}
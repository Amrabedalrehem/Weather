package com.example.data

import com.example.data.datasource.local.DataSourceLocal
import com.example.data.datasource.local.IDataSourceLocal
import com.example.data.datasource.remote.DataSourceRemote
import com.example.data.datasource.remote.IDataSourceRemote
import com.example.data.datasource.sharedPreference.DataStorePermission
import com.example.data.datasource.sharedPreference.DataStoreSettings
import com.example.data.datasource.sharedPreference.IDataStorePermission
import com.example.data.datasource.sharedPreference.IDataStoreSettings
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
    private val local: IDataSourceLocal,
    private val remote: IDataSourceRemote,
    private val settings: IDataStoreSettings,
    private val permission: IDataStorePermission
) : IRepository {

    override val wasPermissionRequested: Flow<Boolean> = permission.wasPermissionRequested
    override suspend fun markPermissionRequested() = permission.markPermissionRequested()

    override val temperatureUnit: Flow<String> = settings.temperatureUnit
    override val windSpeedUnit: Flow<String> = settings.windSpeedUnit
    override val language: Flow<String> = settings.language
    override val locationType: Flow<String> = settings.locationType
    override val theme: Flow<String> = settings.theme
    override val latitude: Flow<Double> = permission.latitude
    override val longitude: Flow<Double> = permission.longitude

    override suspend fun saveLocation(lat: Double, lon: Double) = permission.saveLocation(lat, lon)
    override suspend fun saveTemperatureUnit(value: String) = settings.saveTemperatureUnit(value)
    override suspend fun saveWindSpeedUnit(value: String) = settings.saveWindSpeedUnit(value)
    override suspend fun saveLanguage(value: String) = settings.saveLanguage(value)
    override suspend fun saveLocationType(value: String) = settings.saveLocationType(value)
    override suspend fun saveTheme(value: String) = settings.saveTheme(value)

    override suspend fun clearSavedLocation() {
        permission.saveLocation(0.0, 0.0)
    }

    private fun mapUnits(unit: String): String = when (unit) {
        "celsius"    -> "metric"
        "fahrenheit" -> "imperial"
        else         -> "standard"
    }

    private fun mapLanguage(lang: String): String = when (lang) {
        "en" -> "en"
        "ar" -> "ar"
        else -> "en"
    }

    override suspend fun getCurrentWeather(): Response<CurrentWeatherDto> {
        val lat   = permission.latitude.first()
        val lon   = permission.longitude.first()
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getCurrentWeather(lat = lat, lon = lon, lang = lang, units = units)
    }

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Response<CurrentWeatherDto> {
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getCurrentWeather(lat = lat, lon = lon, lang = lang, units = units)
    }

    override suspend fun getHourlyForecast(city: String): Response<HourlyForecastResponse> {
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getHourlyForecast(city = city, units = units, lang = lang)
    }

    override suspend fun getFiveDayForecast(city: String): Response<FiveDayForecastResponse> {
        val lat   = permission.latitude.first()
        val lon   = permission.longitude.first()
        val lang  = mapLanguage(settings.language.first())
        val units = mapUnits(settings.temperatureUnit.first())
        return remote.getFiveDayForecast(
            city = city, lat = lat, lon = lon, units = units, lang = lang
        )
    }

    override fun getAllFavourites() = local.getAllFavourites()
    override fun getFavouriteById(id: Int) = local.getFavouriteById(id)
    override suspend fun insert(location: FavouriteLocationCache) = local.insert(location)
    override suspend fun delete(location: FavouriteLocationCache) = local.delete(location)
    override fun getHomeWeather() = local.getHomeWeather()
    override suspend fun insertHomeWeather(cache: HomeWeatherCache) = local.insertHomeWeather(cache)
    override fun getAllAlarms() = local.getAllAlarms()
    override suspend fun insertAlarm(alarm: AlarmEntity): Long = local.insertAlarm(alarm)
    override suspend fun deleteAlarm(alarm: AlarmEntity) = local.deleteAlarm(alarm)
    override suspend fun updateAlarm(alarm: AlarmEntity) = local.updateAlarm(alarm)
    override suspend fun toggleAlarm(id: Int, isActive: Boolean) = local.toggleAlarm(id, isActive)
    override suspend fun getAlarmById(id: Int) = local.getAlarmById(id)
}
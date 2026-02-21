package com.example.data

import com.example.data.datasource.local.DataSourceLocal
import com.example.data.datasource.remote.DataSourceRemote
import com.example.data.datasource.sharedPreference.DataStorePermission
import com.example.data.datasource.sharedPreference.DataStoreSettings
import com.example.data.model.weather.CurrentWeatherDto
import com.example.data.model.weather.FiveDayForecastResponse
import com.example.data.model.weather.HourlyForecastResponse
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
        "Celsius (°C)"    -> "metric"
        "Fahrenheit (°F)" -> "imperial"
        else              -> "standard"
    }

    private fun mapLanguage(lang: String): String = when (lang) {
        "English"  -> "en"
        "العربية"  -> "ar"
        else       -> "en"
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
}
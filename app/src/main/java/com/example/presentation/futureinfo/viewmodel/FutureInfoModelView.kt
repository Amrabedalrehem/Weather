package com.example.presentation.futureinfo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.Repository
import com.example.data.model.dto.FiveDayForecastResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FutureInfoState {
    object Loading : FutureInfoState()
    data class Success(val days: List<BadWeatherDay>) : FutureInfoState()
    object Error : FutureInfoState()
}

data class BadWeatherDay(
    val dayName: String,
    val date: String,
    val description: String,
    val temp: Int, val icon: String, val severity: Severity)

enum class Severity { MODERATE, HIGH, EXTREME }

class FutureInfoViewModel(
    application: Application,
    private val repository: IRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<FutureInfoState>(FutureInfoState.Loading)
    val state: StateFlow<FutureInfoState> = _state

    private val badConditions = mapOf(
        "thunderstorm" to Severity.EXTREME,
        "tornado"      to Severity.EXTREME,
        "hurricane"    to Severity.EXTREME,
        "snow"         to Severity.HIGH,
        "blizzard"     to Severity.HIGH,
        "rain"         to Severity.HIGH,
        "drizzle"      to Severity.MODERATE,
        "fog"          to Severity.MODERATE,
        "mist"         to Severity.MODERATE,
        "haze"         to Severity.MODERATE,
        "smoke"        to Severity.MODERATE,
        "dust"         to Severity.MODERATE,
        "sand"         to Severity.HIGH,
        "squall"       to Severity.HIGH
    )

    fun fetchForecast() {
        viewModelScope.launch {
            _state.value = FutureInfoState.Loading
            try {
                val currentResponse = repository.getCurrentWeather()
                val cityName = if (currentResponse.isSuccessful) {
                    currentResponse.body()?.name ?: "Cairo"
                } else "Cairo"

                val response = repository.getFiveDayForecast(cityName)
                if (response.isSuccessful) {
                    _state.value = FutureInfoState.Success(parseBadDays(response.body()))
                } else {
                    _state.value = FutureInfoState.Error
                }
            } catch (e: Exception) {
                _state.value = FutureInfoState.Error
            }
        }
    }

    private fun parseBadDays(body: FiveDayForecastResponse?): List<BadWeatherDay> {
        if (body == null) return emptyList()

        return body.fiveDay
            .mapNotNull { entry ->
                val desc     = entry.weather.firstOrNull()?.description?.lowercase() ?: ""
                val severity = getSeverity(desc) ?: return@mapNotNull null

                val cal = java.util.Calendar.getInstance().apply {
                    timeInMillis = entry.dt * 1000L
                }

                BadWeatherDay(
                    dayName     = getDayName(cal),
                    date        = getFormattedDate(cal),
                    description = entry.weather.firstOrNull()?.description
                        ?.replaceFirstChar { it.uppercase() } ?: "",
                    temp        = entry.temp.day.toInt(),
                    icon        = entry.weather.firstOrNull()?.icon ?: "",
                    severity    = severity
                )
            }
            .filter { it.dayName != getTodayName() }
            .take(5)
    }

    private fun getSeverity(desc: String): Severity? {
        for ((keyword, severity) in badConditions) {
            if (desc.contains(keyword)) return severity
        }
        return null
    }

    private fun getDayName(cal: java.util.Calendar): String =
        java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(cal.time)

    private fun getTodayName(): String =
        java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(java.util.Date())

    private fun getFormattedDate(cal: java.util.Calendar): String =
        java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault()).format(cal.time)
}

class FutureInfoViewModelFactory(
    private val application: Application,
    private val repository: IRepository
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return FutureInfoViewModel(application, repository) as T
    }
}
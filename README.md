 
# 🌦 Weather Forecast Android App

Android mobile application that displays real-time weather data based on the user’s current location or a selected location from the map.
The app also allows users to save favorite locations and set customizable weather alerts.

---

## 📱 Features

### 🏠 Home Screen

* Display current temperature
* Current date & time
* Humidity
* Wind speed
* Atmospheric pressure
* Cloud percentage
* City name
* Weather icon
* Weather description (Clear sky, Light rain, etc.)
* Hourly forecast for the current day
* 5-day forecast

---

### ⚙️ Settings Screen

Users can customize:

#### 📍 Location Options

* Get location using GPS
* Select a specific location from the map

#### 🌡 Units

* Temperature units: Kelvin / Celsius / Fahrenheit
* Wind speed units: Meter/sec / Miles/hour

#### 🌐 Language

* English
* Arabic

---

### ⭐ Favorites Screen

* Add new favorite location using:

  * Map selection
  * Auto-complete search
* View full forecast for selected favorite
* Remove saved locations
* Floating Action Button (FAB) to add new location

---

### 🚨 Weather Alerts Screen

* Create weather alerts for:

  * Rain
  * Snow
  * Fog
  * Wind
  * Extreme temperatures
* Set:

  * Alert duration
  * Notification only OR Alarm sound
* Ability to stop/disable alert

---

## 🛠 Technologies Used

* Kotlin
* MVVM Architecture
* Retrofit
* Coroutines
* Room Database
* Google Maps SDK
* Location Services
* WorkManager / AlarmManager
* OpenWeatherMap API

---

## 🌍 Weather API

Weather data is fetched from:

[https://api.openweathermap.org/data/2.5/forecast](https://api.openweathermap.org/data/2.5/forecast)

OpenWeatherMap API is used to:

* Get 5-day / 3-hour forecast
* Retrieve weather condition details
* Fetch weather icons

---

## 🏗 Architecture

The project follows **MVVM Architecture Pattern** with:

* Repository Pattern
* Clean separation of concerns
* Local caching using Room
* Background processing using WorkManager

 
  

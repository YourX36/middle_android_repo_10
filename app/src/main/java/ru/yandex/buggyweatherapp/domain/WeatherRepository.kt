package ru.yandex.buggyweatherapp.domain

import ru.yandex.buggyweatherapp.model.Location
import ru.yandex.buggyweatherapp.model.WeatherData

interface WeatherRepository {
    suspend fun getWeatherData(location: Location, callback: (WeatherData?, Exception?) -> Unit)
    suspend fun getWeatherByCity(cityName: String, callback: (WeatherData?, Exception?) -> Unit)
}
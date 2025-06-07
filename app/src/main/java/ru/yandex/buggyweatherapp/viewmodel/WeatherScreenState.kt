package ru.yandex.buggyweatherapp.viewmodel

import ru.yandex.buggyweatherapp.model.WeatherData

sealed class WeatherScreenState {
    data object Idle : WeatherScreenState()
    data class Success(val weatherData: WeatherData) : WeatherScreenState()
    data class Error(val message: String) : WeatherScreenState()
    data object Loading : WeatherScreenState()
}
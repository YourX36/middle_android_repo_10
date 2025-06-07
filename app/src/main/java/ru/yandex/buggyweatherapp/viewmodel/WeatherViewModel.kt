package ru.yandex.buggyweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.R
import ru.yandex.buggyweatherapp.model.Location
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.repository.LocationRepository
import ru.yandex.buggyweatherapp.repository.WeatherRepository
import ru.yandex.buggyweatherapp.utils.StringProvider

class WeatherViewModel(
    private val stringProvider: StringProvider,
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    
    private val _weatherState = MutableStateFlow<WeatherScreenState>(WeatherScreenState.Idle)
    val weatherState = _weatherState.asStateFlow()

    private var cachedWeatherData : WeatherData? = null

    private var refreshJob: Job? = null
    
    
    init {
        startAutoRefresh()
    }
    
    
    fun fetchCurrentLocationWeather() {
        showLoading()
        viewModelScope.launch {
            locationRepository.getCurrentLocation { location ->
                location?.let {
                    getWeatherForLocation(it)
                    getCityNameFromLocation(it)
                } ?: run {
                    showError(stringProvider.getString(R.string.unable_to_get_current_location))
                }
            }
        }
    }

    fun refreshData() {
        cachedWeatherData?.cityName?.let {
            searchWeatherByCity(it)
        } ?: run {
            fetchCurrentLocationWeather()
        }
    }

    private fun getCityNameFromLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.getCityNameFromLocation(location)?.let { cityName ->
                cachedWeatherData?.let { cachedData ->
                    val updatedData = cachedData.copy(cityName = cityName)
                    updateSuccess(updatedData)
                }
            }
        }
    }
    
    private fun getWeatherForLocation(location: Location) {
        showLoading()
        viewModelScope.launch {
            weatherRepository.getWeatherData(location) { data, exception ->
                data?.let { _data ->
                    updateSuccess(_data)
                } ?: run {
                    showError(exception?.message ?: stringProvider.getString(R.string.unknown_error))
                }
            }
        }
    }
    
    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            showError(stringProvider.getString(R.string.city_name_cannot_be_empty))
            return
        }

        showLoading()

        viewModelScope.launch {
            weatherRepository.getWeatherByCity(city) { data, exception ->
                data?.let {
                    updateSuccess(data)
                } ?: run {
                    showError(exception?.message ?: stringProvider.getString(R.string.unknown_error))
                }
            }
        }

    }

    private fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            delay(AUTO_REFRESH_TIMER)
            while (isActive) {
                refreshData()
                delay(AUTO_REFRESH_TIMER)
            }
        }
    }

    
    fun toggleFavorite() {
        cachedWeatherData?.let { cachedData ->
            val updatedData = cachedData.copy(isFavorite = !cachedData.isFavorite)
            updateSuccess(updatedData)
        }
    }

    private fun updateSuccess(data: WeatherData) {
        cachedWeatherData = data
        _weatherState.update { WeatherScreenState.Success(data) }
    }

    private fun showLoading() {
        _weatherState.update { WeatherScreenState.Loading }
    }

    private fun showError(message: String) {
        _weatherState.update { WeatherScreenState.Error(message) }
    }
    
    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }

    private companion object {
        const val AUTO_REFRESH_TIMER = 60_000L
    }
}
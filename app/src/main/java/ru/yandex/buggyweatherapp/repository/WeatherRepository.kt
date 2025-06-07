package ru.yandex.buggyweatherapp.repository

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.yandex.buggyweatherapp.api.WeatherApiService
import ru.yandex.buggyweatherapp.model.Location
import ru.yandex.buggyweatherapp.model.WeatherData

class WeatherRepository(
    private val weatherApi: WeatherApiService
) {

    private var cachedWeatherData: WeatherData? = null

    suspend fun getWeatherData(location: Location, callback: (WeatherData?, Exception?) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    weatherApi.getCurrentWeather(location.latitude, location.longitude).execute()
                if (response.isSuccessful && response.body() != null) {
                    val weatherData = parseWeatherData(response.body()!!, location)
                    cachedWeatherData = weatherData
                    callback(weatherData, null)
                } else {
                    callback(null, Exception("Empty data."))
                }
            } catch (e: Exception) {
                callback(null, e)
            }
        }
    }

    suspend fun getWeatherByCity(cityName: String, callback: (WeatherData?, Exception?) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApi.getWeatherByCity(cityName).execute()
                if (response.isSuccessful && response.body() != null) {
                    val json = response.body()!!
                    val location = extractLocationFromResponse(json)
                    val weatherData = parseWeatherData(json, location)
                    callback(weatherData, null)
                } else {
                    callback(null, Exception("Error fetching weather data for city: $cityName"))
                }
            } catch (e: Exception) {
                callback(null, e)
            }
        }
    }

    private fun parseWeatherData(json: JsonObject, location: Location): WeatherData {

        val main = json.getAsJsonObject("main")
        val wind = json.getAsJsonObject("wind")
        val sys = json.getAsJsonObject("sys")
        val weather = json.getAsJsonArray("weather").get(0).asJsonObject
        val clouds = json.getAsJsonObject("clouds")

        return WeatherData(
            cityName = json.get("name").asString,
            country = sys.get("country").asString,
            temperature = main.get("temp").asDouble,
            feelsLike = main.get("feels_like").asDouble,
            minTemp = main.get("temp_min").asDouble,
            maxTemp = main.get("temp_max").asDouble,
            humidity = main.get("humidity").asInt,
            pressure = main.get("pressure").asInt,
            windSpeed = wind.get("speed").asDouble,
            windDirection = if (wind.has("deg")) wind.get("deg").asInt else 0,
            description = weather.get("description").asString,
            icon = weather.get("icon").asString,
            cloudiness = clouds.get("all").asInt,
            sunriseTime = sys.get("sunrise").asLong,
            sunsetTime = sys.get("sunset").asLong,
            timezone = json.get("timezone").asInt,
            timestamp = json.get("dt").asLong,
            rawApiData = json.toString(),
            rain = if (json.has("rain") && json.getAsJsonObject("rain").has("1h"))
                json.getAsJsonObject("rain").get("1h").asDouble else null,
            snow = if (json.has("snow") && json.getAsJsonObject("snow").has("1h"))
                json.getAsJsonObject("snow").get("1h").asDouble else null
        )
    }

    private fun extractLocationFromResponse(json: JsonObject): Location {
        val coord = json.getAsJsonObject("coord")
        val lat = coord.get("lat").asDouble
        val lon = coord.get("lon").asDouble
        val name = json.get("name").asString

        return Location(lat, lon, name)
    }
}
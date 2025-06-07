package ru.yandex.buggyweatherapp.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    /**
     * Тут бы я вынес файлы в local.properties или на бэкенд, в котором через переменные вытаскивал
     * при настройке CI/CD
     *
     * WEATHER_API_KEY=8fd9a0f2216e2bc16a09102e2af8ab1d
     * WEATHER_BASE_URL=http://api.openweathermap.org/data/2.5/
     *
     * В build.gradle.kts :
     *
     * android {
     *     defaultConfig {
     *         buildConfigField("String", "WEATHER_API_KEY", "\"${properties["WEATHER_API_KEY"]}\"")
     *         buildConfigField("String", "WEATHER_BASE_URL", "\"${properties["WEATHER_BASE_URL"]}\"")
     *     }
     * }
     *
     * Но не знаю как это сделать для проверки работы, ведь локальный ключ будет отсутствовать и
     * приложение не запустится
     */

    companion object {
        const val API_KEY = "8fd9a0f2216e2bc16a09102e2af8ab1d"
        const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
    }
    
    
    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): Call<JsonObject>
    
    @GET("weather")
    fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): Call<JsonObject>
    
    @GET("forecast")
    fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): Call<JsonObject>
}
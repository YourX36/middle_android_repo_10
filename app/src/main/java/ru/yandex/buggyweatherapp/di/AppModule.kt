package ru.yandex.buggyweatherapp.di

import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.yandex.buggyweatherapp.api.RetrofitInstance
import ru.yandex.buggyweatherapp.api.WeatherApiService
import ru.yandex.buggyweatherapp.repository.LocationRepository
import ru.yandex.buggyweatherapp.repository.WeatherRepository
import ru.yandex.buggyweatherapp.utils.AndroidStringProvider
import ru.yandex.buggyweatherapp.utils.StringProvider
import ru.yandex.buggyweatherapp.viewmodel.WeatherViewModel

val appModule = module {
    single<Context> { androidApplication().applicationContext }

    single<LocationRepository> { LocationRepository(get())}
    single<WeatherRepository> { WeatherRepository(get())}
    single<StringProvider> { AndroidStringProvider(get()) }

    single<WeatherApiService> { RetrofitInstance.weatherApi }

    viewModel<WeatherViewModel> { WeatherViewModel(get(), get(), get())}
}
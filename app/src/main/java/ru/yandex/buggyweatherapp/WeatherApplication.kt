package ru.yandex.buggyweatherapp

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.yandex.buggyweatherapp.di.appModule
import ru.yandex.buggyweatherapp.utils.ImageLoader
import ru.yandex.buggyweatherapp.utils.LocationTracker

class WeatherApplication : Application() {
    
    
    companion object {
        lateinit var appContext: Context
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        
        
        appContext = this

        startKoin{
            androidContext(this@WeatherApplication)
            modules(appModule)
        }
        
        ImageLoader.initialize(this)
        LocationTracker.getInstance(this)
    }
}
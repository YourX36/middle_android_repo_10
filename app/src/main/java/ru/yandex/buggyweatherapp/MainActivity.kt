package ru.yandex.buggyweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.yandex.buggyweatherapp.ui.screens.WeatherScreen
import ru.yandex.buggyweatherapp.ui.theme.BuggyWeatherAppTheme
import ru.yandex.buggyweatherapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val weatherViewModel by viewModel<WeatherViewModel>()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                weatherViewModel.fetchCurrentLocationWeather()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                weatherViewModel.fetchCurrentLocationWeather()
            }

            else -> {
                Toast.makeText(
                    this,
                    getString(R.string.location_permission_denied), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLocationPermissions()

        enableEdgeToEdge()

        setContent {
            BuggyWeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        viewModel = weatherViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        if (hasLocationPermissions()) {
            weatherViewModel.fetchCurrentLocationWeather()
        } else {
            locationPermissionRequest.launch(LOCATION_PERMISSIONS)
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
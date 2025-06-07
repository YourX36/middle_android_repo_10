package ru.yandex.buggyweatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import ru.yandex.buggyweatherapp.ui.screens.WeatherScreen
import ru.yandex.buggyweatherapp.ui.theme.BuggyWeatherAppTheme
import ru.yandex.buggyweatherapp.utils.RequestLocationPermissionEffect
import ru.yandex.buggyweatherapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BuggyWeatherAppTheme {
                enableEdgeToEdge()

                val viewModel: WeatherViewModel = koinViewModel()
                val context = LocalContext.current

                RequestLocationPermissionEffect(
                    onGranted = { viewModel.fetchCurrentLocationWeather() },
                    onDenied = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.location_permission_denied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
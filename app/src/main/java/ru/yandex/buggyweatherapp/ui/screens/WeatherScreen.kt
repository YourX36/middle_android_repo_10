package ru.yandex.buggyweatherapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.yandex.buggyweatherapp.R
import ru.yandex.buggyweatherapp.model.WeatherData
import ru.yandex.buggyweatherapp.utils.WeatherIconMapper
import ru.yandex.buggyweatherapp.viewmodel.WeatherScreenState
import ru.yandex.buggyweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {

    val weatherState by viewModel.weatherState.collectAsState()
    
    var searchText by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text(stringResource(R.string.search_city)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { 
                    
                    viewModel.searchWeatherByCity(searchText) 
                }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { 
                viewModel.searchWeatherByCity(searchText) 
            })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when(weatherState) {
            is WeatherScreenState.Error -> {
                val error = (weatherState as WeatherScreenState.Error).message
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            WeatherScreenState.Idle -> {}
            WeatherScreenState.Loading -> {
                Text(stringResource(R.string.loading_weather_data))
            }
            is WeatherScreenState.Success -> {
                val data = (weatherState as WeatherScreenState.Success).weatherData
                WeatherCard(
                    weather = data,
                    onFavoriteClick = {
                        viewModel.toggleFavorite()
                    }
                ) {
                    viewModel.refreshData()
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    weather: WeatherData,
    onFavoriteClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weather.cityName,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Row {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (weather.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorite)
                        )
                    }
                    
                    IconButton(onClick = onRefreshClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            
            Text(
                text = stringResource(R.string.temperature, weather.temperature.toString()),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = stringResource(R.string.feels_like, weather.feelsLike.toString()),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = stringResource(R.string.description, weather.description.replaceFirstChar { it.uppercase() }),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = stringResource(R.string.humidity, weather.humidity.toString()),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = stringResource(R.string.wind,weather.windSpeed.toString()),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                
                Text(
                    text = stringResource(R.string.sunrise, WeatherIconMapper.formatTimestamp(weather.sunriseTime)),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = stringResource(R.string.sunset,WeatherIconMapper.formatTimestamp(weather.sunsetTime)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRefreshClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.refresh_weather))
            }
        }
    }
}
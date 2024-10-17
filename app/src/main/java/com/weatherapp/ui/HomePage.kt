package com.weatherapp.ui

import android.content.Context
import android.icu.text.DecimalFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.model.Forecast
import com.weatherapp.repo.Repository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    context: Context,
    repository: Repository
) {
    val city = if (viewModel.city != null) viewModel.cities[viewModel.city] else null
    val isMonitored = city?.isMonitored == true
    val icon = if (isMonitored) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder

    if (city != null) {
        if (city.weather == null)  viewModel.loadWeather(city)
        if (city.forecast == null) viewModel.loadForecast(city)
    }

    Column {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = "Monitor?",
                modifier = Modifier
                            .size(32.dp)
                            .clickable(enabled = viewModel.city != null) {
                                repository.update(city!!.copy(isMonitored = !city.isMonitored))
                                // repository.update(viewModel.city!!.copy(isMonitored = !viewModel.city!!.isMonitored!!))
                            }
            )

            AsyncImage(
                model = city?.weather?.imgUrl,
                modifier = Modifier.size(100.dp),
                error = painterResource(id = R.drawable.loading),
                contentDescription = "Imagem"
            )
            val format = DecimalFormat("#.0")

            Column {
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = city?.name ?: "Selecione uma cidade...",
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = city?.weather?.desc ?: "...",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Temp: "+ city?.weather?.temp +"ºC",
                    fontSize = 20.sp
                )
            }
        }

        city?.forecast?.let { forecasts ->
            LazyColumn {
                items(forecasts) { forecast ->
                    ForecastItem(
                        forecast = forecast,
                        onClick = {},
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun ForecastItem(
    forecast: Forecast,
    onClick: (Forecast) -> Unit,
    modifier: Modifier = Modifier
) {
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)

    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp).clickable(onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = forecast.imgUrl,
            modifier = Modifier.size(40.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(modifier = Modifier, text = forecast.weather, fontSize = 20.sp)
            Row {
                Text(modifier = Modifier, text = forecast.date, fontSize = 16.sp)
                Spacer(modifier = Modifier.size(12.dp))

                Text(modifier = Modifier, text = "Min: $tempMin ºC", fontSize = 14.sp)
                Spacer(modifier = Modifier.size(12.dp))

                Text(modifier = Modifier, text = "Max: $tempMax ºC", fontSize = 14.sp)
            }
        }
    }
}
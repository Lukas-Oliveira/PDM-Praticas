package com.weatherapp.ui

import android.app.Activity
import android.content.Context
import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.model.Forecast

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    context: Context
) {
    var activity = LocalContext.current as? Activity

    Column {
        Row {
            AsyncImage(
                model = viewModel.city?.weather?.imgUrl,
                modifier = Modifier.size(100.dp),
                error = painterResource(id = R.drawable.loading),
                contentDescription = "Imagem"
            )
            val format = DecimalFormat("#.0")

            Column {
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = viewModel.city?.name ?: "Selecione uma cidade...",
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = viewModel.city?.weather?.desc ?: "...",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Temp: "+ viewModel.city?.weather?.temp + "ºC",
                    fontSize = 20.sp
                )
            }
        }

        if (viewModel.city == null || viewModel.city!!.forecast == null)
            return

        LazyColumn {
            items(viewModel.city!!.forecast!!) { forecast ->
                ForecastItem(forecast, onClick = {}, modifier = modifier)
            }
        }
    }

    /*
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.teal_700))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Home",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
    */
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
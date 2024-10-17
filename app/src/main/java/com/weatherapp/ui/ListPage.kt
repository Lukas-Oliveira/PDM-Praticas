package com.weatherapp.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.model.City
import com.weatherapp.repo.Repository
import com.weatherapp.ui.nav.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    context: Context,
    navCtrl: NavHostController,
    repository: Repository
) {
    val activity = LocalContext.current as? Activity

    LazyColumn (
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        items(viewModel.cities.keys.toList()) { cityName ->

            val city = viewModel.cities[cityName]!!
            if (city.weather == null) {
                viewModel.loadWeather(city)
            }

            CityItem(
                city = city,
                onClick = {
                    viewModel.city = city.name + ""
                    navigateTo(navCtrl, BottomNavItem.HomePage.route)
                    Toast.makeText(activity, "Clique em ${city.name}.", Toast.LENGTH_LONG).show()
                },
                onClose = {
                    Toast.makeText(activity, "Removendo ${city.name}.", Toast.LENGTH_LONG).show()
                    repository.remove(city)
                },
                repository = repository
            )
        }
    }
}

fun navigateTo(navCtrl: NavHostController, route: String) {
    navCtrl.navigate(route)
}

@Composable
fun CityItem(
    city: City,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    repository: Repository
) {
    val icon = if (city.isMonitored) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Monitor?",
            modifier = Modifier
                        .size(32.dp)
                        .clickable(enabled = true) {
                            repository.update(city.copy(isMonitored = !city.isMonitored))
                        }
        )
        AsyncImage(
            model = city.weather?.imgUrl,
            modifier = Modifier.size(75.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = modifier.weight(1f)) {
            Text(
                modifier = Modifier,
                text = city.name,
                fontSize = 24.sp
            )
            Text(
                modifier = Modifier,
                text = city.weather?.desc ?: "Carregando...",
                fontSize = 16.sp
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close"
            )
        }
    }
}

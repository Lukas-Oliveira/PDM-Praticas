package com.weatherapp.ui

import androidx.compose.runtime.toMutableStateList
import com.google.android.gms.maps.model.LatLng

data class City(
    val name: String,
    val weather: String,
    val location: LatLng? = null
)

fun getCities() = List(30) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")
}

class MainViewModel : ViewModelBase() {

    private val _cities = getCities().toMutableStateList()
    val cities: List<City>
        get() = _cities

    fun remove(city: City) {
        _cities.remove(city)
    }

    fun add(city: String, location: LatLng? = null) {
        _cities.add(City(city, "Carregando clima...", location))
    }
}
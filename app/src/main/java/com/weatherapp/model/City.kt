package com.weatherapp.model

import com.google.android.gms.maps.model.LatLng

data class City (
    val name: String,
    var weather: String,
    var location: LatLng? = null
)

fun getCities() = List(30) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")
}
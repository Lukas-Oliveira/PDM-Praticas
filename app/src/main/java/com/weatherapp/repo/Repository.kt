package com.weatherapp.repo

import com.google.android.gms.maps.model.LatLng
import com.weatherapp.api.WeatherService
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.db.fb.toFBCity
import com.weatherapp.db.local.LocalDB
import com.weatherapp.db.local.toCity
import com.weatherapp.model.City
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class Repository(
    private val firebaseDatabase: FBDatabase,
    private val localDB: LocalDB,
    private val service: WeatherService
) {
    private var ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var cityMap = emptyMap<String, City>() // Cache das cidades

    val cities = localDB.cities.map { it.map { it.toCity() } }
    val user = firebaseDatabase.user.map { it.toUser() }

    init {
        ioScope.launch {
            firebaseDatabase.cities.collect { FBCityList ->
                val cityList = FBCityList.map { it.toCity() }
                val nameList = cityList.map { it.name }

                val deletedCities = cityMap.filter { it.key !in nameList }
                val updatedCities = cityList.filter { it.name in cityMap.keys }
                val newCities = cityList.filter { it.name !in cityMap.keys }

                newCities.forEach { localDB.insert(it) }
                updatedCities.forEach { localDB.update(it) }
                deletedCities.forEach { localDB.delete(it.value) }

                cityMap = cityList.associateBy { it.name }
            }
        }
    }

    fun addCity(name: String) = ioScope.launch {
        val location = service.getLocation(name) ?: return@launch
        val city = City(name = name, location = location)
        firebaseDatabase.add(city.toFBCity())
    }

    fun addCity(lat: Double, lng: Double) = ioScope.launch {
        val name = service.getName(lat, lng) ?: return@launch
        val city = City(name = name, location = LatLng(lat, lng))
        firebaseDatabase.add(city.toFBCity())
    }

    fun remove(city: City) = firebaseDatabase.remove(city.toFBCity())
    fun update(city: City) = firebaseDatabase.update(city.toFBCity())
}

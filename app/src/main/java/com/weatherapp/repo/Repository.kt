package com.weatherapp.repo

import com.google.android.gms.maps.model.LatLng
import com.weatherapp.api.WeatherService
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.model.City
import com.weatherapp.model.User

class Repository(private var listener: Listener): FBDatabase.Listener {

    private var firebaseDatabase = FBDatabase(this)
    private var weatherService = WeatherService()

    interface Listener {
        fun onUserLoaded(user: User)
        fun onCityAdded(city: City)
        fun onCityRemoved(city: City)
    }

    fun addCity(name: String) {
        weatherService.getLocation(name) { lat, lng ->
            firebaseDatabase.add(City(name = name, weather = "loading...", location = LatLng(lat ?: 0.0, lng ?: 0.0)))
        }
    }

    fun addCity(lat: Double, lng: Double) {
        weatherService.getName(lat, lng) { name ->
            firebaseDatabase.add(City(name = name ?: "NOT FOUND", location = LatLng(lat, lng)))
        }
    }

    fun remove(city: City) {
        firebaseDatabase.remove(city)
    }

    fun register(username: String, email: String) {
        firebaseDatabase.register(User(username, email))
    }

    override fun onUserLoaded(user: User) {
        listener.onUserLoaded(user)
    }

    override fun onCityAdded(city: City) {
        listener.onCityAdded(city)
    }

    override fun onCityRemoved(city: City) {
        listener.onCityRemoved(city)
    }
}
package com.weatherapp.repo

import com.google.android.gms.maps.model.LatLng
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.model.City
import com.weatherapp.model.User

class Repository(private var listener: Listener): FBDatabase.Listener {

    private var firebaseDatabase = FBDatabase(this)

    interface Listener {
        fun onUserLoaded(user: User)
        fun onCityAdded(city: City)
        fun onCityRemoved(city: City)
    }

    fun addCity(name: String) {
        firebaseDatabase.add(City(name, LatLng(0.0, 0.0)))
    }

    fun addCity(lat: Double, long: Double) {
        firebaseDatabase.add(City("Cidade@$lat:$long", LatLng(lat, long)))
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
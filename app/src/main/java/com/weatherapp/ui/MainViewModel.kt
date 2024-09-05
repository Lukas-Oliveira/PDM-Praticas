package com.weatherapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.weatherapp.model.City
import com.weatherapp.model.User
import com.weatherapp.model.getCities

class MainViewModel : ViewModel() {

    private val _user = mutableStateOf(User("", ""))
    private val _cities = getCities().toMutableStateList()
    private var _loggedIn = mutableStateOf(false)

    val loggedIn: Boolean
        get() = _loggedIn.value

    val user: User
        get() = _user.value

    private val listener = FirebaseAuth.AuthStateListener {
        firebaseAuth ->
        _loggedIn.value = firebaseAuth.currentUser != null
    }

    init {
        listener.onAuthStateChanged(Firebase.auth)
        Firebase.auth.addAuthStateListener(listener)
    }

    override fun onCleared() {
        Firebase.auth.removeAuthStateListener(listener)
    }

    val cities: List<City>
        get() = _cities

    fun remove(city: City) {
        _cities.remove(city)
    }

    fun add(city: String, location: LatLng? = null) {
        // cities.add(City(city, "Carregando clima..."))
        _cities.add(City(city, "Carregando clima...", location))
    }
}
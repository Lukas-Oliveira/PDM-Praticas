package com.weatherapp.ui

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.model.City
import com.weatherapp.model.User
import com.weatherapp.repo.Repository

class MainViewModel : ViewModelBase(), Repository.Listener {

    private val _user = mutableStateOf(User("", ""))
    val user: User
        get() = _user.value

    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList()

    override fun onUserLoaded(user: User) {
        _user.value = user
    }

    override fun onCityAdded(city: City) {
        _cities[city.name] = city
    }

    override fun onCityRemoved(city: City) {
        _cities.remove(city.name)
    }
}
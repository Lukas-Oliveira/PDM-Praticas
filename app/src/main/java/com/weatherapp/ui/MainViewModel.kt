package com.weatherapp.ui

import androidx.compose.runtime.mutableStateOf
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.model.City
import com.weatherapp.model.User

// fun getCities() = List(30) { i ->
//     City(name = "Cidade $i", weather = "Carregando clima...")
// }

class MainViewModel : ViewModelBase(), FBDatabase.Listener {

    private val _user = mutableStateOf(User("", ""))
    val user: User
        get() = _user.value

    private val _cities = ArrayList<City>()
    val cities: List<City>
        get() = _cities

    override fun onUserLoaded(user: User) {
        _user.value = user
    }

    override fun onCityAdded(city: City) {
        _cities.add(city)
    }

    override fun onCityRemoved(city: City) {
        _cities.remove(city)
    }
}
package com.weatherapp.ui

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.weatherapp.model.City
import com.weatherapp.model.User
import com.weatherapp.repo.Repository

class MainViewModel : ViewModelBase(), Repository.Listener {

    private var _city = mutableStateOf<City?>(null)
    var city: City?
        get() = _city.value
        set(tmp) {
            _city.value = tmp?.copy()
        }

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

    override fun onCityUpdated(city: City) {
        _cities.remove(city.name)
        _cities[city.name] = city.copy()

        if (_city.value?.name == city.name) {
            _city.value = city.copy(
                weather  = if (city.weather != null)  city.weather  else _city.value?.weather,
                forecast = if (city.forecast != null) city.forecast else _city.value?.forecast
            )
        }
    }

    override fun onUserSignOut()
    {}
}
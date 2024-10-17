package com.weatherapp.ui

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.api.WeatherService
import com.weatherapp.api.toForecast
import com.weatherapp.api.toWeather
import com.weatherapp.model.City
import com.weatherapp.model.User
import com.weatherapp.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
    private val weatherService: WeatherService
): ViewModel() {

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    private var _city = mutableStateOf<String?>(null)
    var city: String?
        get() = _city.value
        set(tmp) {
            _city.value = tmp
        }

    private val _cities = mutableStateMapOf<String, City>()
    val cities: Map<String, City>
        get() = _cities.toMap()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            repository.user.collect { user ->
                _user.value = user.copy()
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            repository.cities.collect { list ->
                val names = list.map { it.name }
                val newCities = list.filter { it.name !in _cities.keys }
                val oldCities = list.filter { it.name in _cities.keys }

                _cities.keys.removeIf { it !in names }
                newCities.forEach { _cities[it.name] = it }
                oldCities.forEach {
                    _cities[it.name] = _cities[it.name]!!.copy(isMonitored = it.isMonitored)
                }
            }
        }
    }

    fun loadWeather(city: City) = viewModelScope.launch {
        city.weather = weatherService.getCurrentWeather(city.name)?.toWeather()
        _cities.remove(city.name)
        _cities[city.name] = city.copy()
    }

    fun loadForecast(city: City) = viewModelScope.launch {
        city.forecast = weatherService.getForecast(city.name)?.toForecast()
        _cities.remove(city.name)
        _cities[city.name] = city.copy()
    }

    fun loadBitmap(city: City) = viewModelScope.launch {


            city.weather!!.bitmap = weatherService.getBitmap(city.weather!!.imgUrl)
            _cities.remove(city.name)
            _cities[city.name] = city.copy()
    }
}
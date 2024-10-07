package com.weatherapp.repo

import com.google.android.gms.maps.model.LatLng
import com.weatherapp.api.WeatherService
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.model.City
import com.weatherapp.model.Forecast
import com.weatherapp.model.User
import com.weatherapp.model.Weather

class Repository(private var listener: Listener): FBDatabase.Listener {

    private var firebaseDatabase = FBDatabase(this)
    private var weatherService = WeatherService()

    interface Listener {
        fun onUserLoaded(user: User)
        fun onCityAdded(city: City)
        fun onCityRemoved(city: City)
        fun onCityUpdated(city: City)
    }

    fun addCity(name: String) {
        weatherService.getLocation(name) { lat, lng ->
            // firebaseDatabase.add(City(name = name, weather = "loading...", location = LatLng(lat ?: 0.0, lng ?: 0.0)))
            firebaseDatabase.add(City(name = name, location = LatLng(lat ?: 0.0, lng ?: 0.0)))
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

    fun loadWeather(city: City) {
        weatherService.getCurrentWeather(city.name) { apiWeather ->
            city.weather = Weather(
                date = apiWeather?.current?.last_updated ?: "...",
                desc = apiWeather?.current?.condition?.text ?: "...",
                temp = apiWeather?.current?.temp_c ?: -1.0,
                imgUrl = "https:"+ apiWeather?.current?.condition?.icon
            )
            listener.onCityUpdated(city)
        }
    }

    fun loadForecast(city: City) {
        weatherService.getForecast(city.name) { result ->
            city.forecast = result?.forecast?.forecastday?.map {
                Forecast(
                    date = it.date ?: "00-00-0000",
                    weather = it.day?.condition?.text ?: "Erro carregando!",
                    tempMin = it.day?.mintemp_c ?: -1.0,
                    tempMax = it.day?.maxtemp_c ?: -1.0,
                    imgUrl = ("https:"+ it.day?.condition?.icon)
                )
            }
            listener.onCityUpdated(city)
        }
    }

    fun loadBitmap(city: City) {
        weatherService.getBitmap(city.weather!!.imgUrl) {
            bitmap ->
                city.weather!!.bitmap = bitmap
                listener.onCityUpdated(city)
        }
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
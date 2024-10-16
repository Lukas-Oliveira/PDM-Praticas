package com.weatherapp.api

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class WeatherService {
    private var weatherAPI: WeatherServiceAPI

    init {
        val retrofitAPI = Retrofit.Builder()
            .baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()

        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }

    suspend fun getName(lat: Double, lng: Double): String? = withContext(Dispatchers.IO) {
        search("$lat,$lng")?.name
    }

    suspend fun getLocation(name: String): LatLng? = withContext(Dispatchers.IO) {
        val response = search(name)
        LatLng(response?.lat ?: 0.0, response?.lon ?: 0.0)
    }

    suspend fun getCurrentWeather(name: String): APICurrentWeather? = withContext(Dispatchers.IO) {
        val call: Call<APICurrentWeather?> = weatherAPI.currentWeather(name)
        call.execute().body()
    }

    suspend fun getForecast(name: String): APIWeatherForecast? = withContext(Dispatchers.IO) {
        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
        call.execute().body()
    }

    suspend fun getBitmap(imgUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        Picasso.get().load(imgUrl).get()
    }

    private fun search(query: String): APILocation? {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        val apiLoc = call.execute().body()

        return if (!apiLoc.isNullOrEmpty()) apiLoc[0] else null
    }
}
package com.weatherapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class City (
    val name: String,
    val location: LatLng? = null,
    var weather: Weather? = null,
    var forecast: List<Forecast>? = null,
    val img_url: String? = null,
    val bitmap: Bitmap? = null
)

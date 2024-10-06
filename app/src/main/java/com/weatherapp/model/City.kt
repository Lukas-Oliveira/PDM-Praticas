package com.weatherapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class City (
    val name: String,
    val location: LatLng? = null,
    val weather: String? = null,
    val img_url: String? = null,
    val bitmap: Bitmap? = null
)

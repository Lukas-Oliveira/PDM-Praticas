package com.weatherapp

import android.app.Application
import android.content.Intent
import com.weatherapp.db.fb.FBAuth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class WeatherApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val fbAuth = FBAuth()
        MainScope().launch {
            fbAuth.currentUserFlow.collect { user ->
                if (user == null) goToLogin()
                else goToMain()
            }
        }
    }

    private fun goToMain() {
        this@WeatherApp.startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }

    private fun goToLogin() {
        this@WeatherApp.startActivity(Intent(this, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }
}
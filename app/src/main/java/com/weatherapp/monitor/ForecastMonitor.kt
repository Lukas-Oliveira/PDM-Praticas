package com.weatherapp.monitor

import android.app.NotificationManager
import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.weatherapp.model.City
import com.weatherapp.model.User
import com.weatherapp.repo.Repository
import java.util.concurrent.TimeUnit

class ForecastMonitor(context: Context): Repository.Listener {

    private val workManager = WorkManager.getInstance(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun updateMonitor(city: City) {

        cancelCity(city)

        if (!city.isMonitored!!)
            return

        val inputData = Data.Builder().putString("city", city.name).build()
        val request = PeriodicWorkRequestBuilder<ForecastWorker>(repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES)
                        .setInitialDelay(duration = 10, timeUnit = TimeUnit.SECONDS)
                        .setInputData(inputData)
                        .build()

        workManager.enqueueUniquePeriodicWork(
            city.name,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    private fun cancelCity(city: City) {
        workManager.cancelUniqueWork(city.name)
        notificationManager.cancel(city.name.hashCode())
    }

    private fun cancelAll() {
        workManager.cancelAllWork()
        notificationManager.cancelAll()
    }

    override fun onUserLoaded(user: User) {}
    override fun onUserSignOut() = cancelAll()
    override fun onCityAdded(city: City) = updateMonitor(city)
    override fun onCityRemoved(city: City) = cancelCity(city)
    override fun onCityUpdated(city: City) = updateMonitor(city)
}
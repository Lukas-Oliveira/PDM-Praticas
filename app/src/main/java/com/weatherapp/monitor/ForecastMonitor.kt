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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ForecastMonitor(context: Context, private val repository: Repository) {

    private val workManager = WorkManager.getInstance(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val monitoring = mutableSetOf<String>()

    init {
        ioScope.launch {
            repository.cities.collect { cities ->
                cities.forEach { updateMonitor(it) }
            }
        }
    }

    private fun updateMonitor(city: City) {

        if (city.name in monitoring && !city.isMonitored) {
            monitoring.remove(city.name)
            cancelCity(city)
            return;
        }

        if (!city.isMonitored)
            return

        monitoring.add(city.name)

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

    fun onUserLoaded(user: User) {}
    fun onUserSignOut() = cancelAll()
    fun onCityAdded(city: City) = updateMonitor(city)
    fun onCityRemoved(city: City) = cancelCity(city)
    fun onCityUpdated(city: City) = updateMonitor(city)
}
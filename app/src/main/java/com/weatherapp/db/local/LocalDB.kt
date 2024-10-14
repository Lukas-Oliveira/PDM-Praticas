package com.weatherapp.db.local

import android.content.Context
import androidx.room.Room
import com.weatherapp.model.City
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalDB(context: Context, databaseName: String) {

    private var roomDB: LocalCityDatabase = Room.databaseBuilder(
        context = context,
        klass = LocalCityDatabase::class.java,
        name = databaseName
    ).build()

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun insert(city: City) = scope.launch {
        roomDB.favCityDao().upsert(city.toLocalCity())
    }

    fun update(city: City) = scope.launch {
        roomDB.favCityDao().upsert(city.toLocalCity())
    }

    fun delete(city: City) = scope.launch {
        roomDB.favCityDao().delete(city.toLocalCity())
    }

    fun getCities(doSomething: (City) -> Unit) = scope.launch {
        roomDB.favCityDao().getCities().collect { list ->
            val mappedList = list.map { it.toCity() }
            mappedList.forEach { doSomething(it) }
        }
    }
}
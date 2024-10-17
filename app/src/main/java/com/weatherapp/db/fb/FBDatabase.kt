package com.weatherapp.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.weatherapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class FBDatabase() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var citiesListReg: ListenerRegistration? = null

    val user: Flow<FBUser>
        get() {
            if (auth.currentUser == null) return emptyFlow()
            return db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .snapshots()
                        .map { it.toObject<FBUser>()!! }
        }

    val cities: Flow<List<FBCity>>
        get() {
            if (auth.currentUser == null) return emptyFlow()
            return db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .collection("cities")
                        .snapshots()
                        .map { snapshot -> snapshot.toObjects(FBCity::class.java) }

        }

    fun register(user: User) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid + "").set(user.toFBUser())
    }

    fun add(city: FBCity) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities").document(city.name!!).set(city)
    }

    fun remove(city: FBCity) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities").document(city.name!!).delete()
    }

    fun update(city: FBCity) {
        if (auth.currentUser == null)
            throw RuntimeException("Not logged in!!")

        val uid = auth.currentUser!!.uid
        val fbCity = city
        val changes = mapOf(
            "lat" to fbCity.lat,
            "lng" to fbCity.lng,
            "monitored" to fbCity.monitored
        )

        db.collection("users").document(uid).collection("cities").document(fbCity.name!!).update(changes)
    }
}
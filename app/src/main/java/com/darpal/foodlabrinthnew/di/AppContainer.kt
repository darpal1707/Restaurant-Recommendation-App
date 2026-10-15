package com.darpal.foodlabrinthnew.di

import android.content.Context
import com.darpal.foodlabrinthnew.data.AuthRepository
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/** Manual dependency container; small enough that a DI framework would only add build complexity. */
class AppContainer(context: Context) {
    private val appScope = CoroutineScope(SupervisorJob())
    private val database = FirebaseDatabase.getInstance()

    val restaurantRepository = RestaurantRepository(database, appScope)
    val reviewRepository = ReviewRepository(database, FirebaseStorage.getInstance())
    val authRepository = AuthRepository(FirebaseAuth.getInstance(), database)
    val preferencesRepository = PreferencesRepository(context)
}

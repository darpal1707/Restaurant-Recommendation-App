package com.darpal.foodlabrinthnew.di

import android.content.Context
import com.darpal.foodlabrinthnew.data.AuthRepository
import com.darpal.foodlabrinthnew.data.LocationProvider
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.ReviewRepository
import com.darpal.foodlabrinthnew.data.osm.OverpassApi
import com.darpal.foodlabrinthnew.data.osm.PlacesCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/** Manual dependency container; small enough that a DI framework would only add build complexity. */
class AppContainer(context: Context) {
    private val appScope = CoroutineScope(SupervisorJob())

    val preferencesRepository = PreferencesRepository(context)
    val locationProvider = LocationProvider(context)
    val restaurantRepository = RestaurantRepository(
        api = OverpassApi(),
        cache = PlacesCache(context),
        location = locationProvider,
        preferences = preferencesRepository,
        scope = appScope,
    )

    // Firebase-backed features (see FeatureFlags.ACCOUNTS_ENABLED). Lazy, so Firebase is only
    // touched if those features are switched on.
    val authRepository by lazy { AuthRepository(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance()) }
    val reviewRepository by lazy { ReviewRepository(FirebaseDatabase.getInstance(), FirebaseStorage.getInstance()) }
}

package com.darpal.foodlabrinthnew.data

import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Loads the `business` node once and keeps it in memory. Every screen (home, search, map,
 * questionnaire) filters this shared list instead of re-downloading hundreds of records as the
 * original activities did.
 */
class RestaurantRepository(
    private val database: FirebaseDatabase,
    private val scope: CoroutineScope,
) {
    private val _restaurants = MutableStateFlow<Resource<List<Restaurant>>>(Resource.Loading)
    val restaurants: StateFlow<Resource<List<Restaurant>>> = _restaurants.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val mutex = Mutex()

    init {
        scope.launch { load() }
    }

    fun refresh() {
        scope.launch { load(userInitiated = true) }
    }

    fun find(id: String): Restaurant? =
        (restaurants.value as? Resource.Success)?.data?.firstOrNull { it.id == id }

    private suspend fun load(userInitiated: Boolean = false) {
        mutex.withLock { loadLocked(userInitiated) }
    }

    private suspend fun loadLocked(userInitiated: Boolean) {
        if (userInitiated) _isRefreshing.value = true
        else if (_restaurants.value !is Resource.Success) _restaurants.value = Resource.Loading
        try {
            val snapshot = database.reference.child(BUSINESS).limitToFirst(LIMIT).get().await()
            val list = withContext(Dispatchers.Default) {
                snapshot.childMaps()
                    .mapNotNull { (key, map) -> Restaurant.fromMap(key, map) }
                    .distinctBy { it.id }
                    .toList()
            }
            _restaurants.value = Resource.Success(list)
        } catch (e: Exception) {
            // Keep showing stale data on a failed refresh.
            if (_restaurants.value !is Resource.Success) _restaurants.value = Resource.Error(e.message)
        } finally {
            _isRefreshing.value = false
        }
    }

    private companion object {
        const val BUSINESS = "business"
        const val LIMIT = 500
    }
}

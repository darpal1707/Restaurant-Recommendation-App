package com.darpal.foodlabrinthnew.data

import com.darpal.foodlabrinthnew.data.model.Area
import com.darpal.foodlabrinthnew.data.model.GeoPoint
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.data.osm.OverpassApi
import com.darpal.foodlabrinthnew.data.osm.PlacesCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

sealed interface PlacesState {
    data object Loading : PlacesState

    /** "Near me" is selected but location permission hasn't been granted. */
    data object NeedsLocation : PlacesState

    data class Error(val message: String?) : PlacesState

    data class Ready(val area: Area, val center: GeoPoint, val restaurants: List<Restaurant>) : PlacesState
}

/**
 * Restaurants around the selected [Area], loaded from OpenStreetMap and shared by every screen.
 * Responses are cached on disk for [CACHE_TTL_MS]; a failed refresh keeps showing cached data.
 */
class RestaurantRepository(
    private val api: OverpassApi,
    private val cache: PlacesCache,
    private val location: LocationProvider,
    private val preferences: PreferencesRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow<PlacesState>(PlacesState.Loading)
    val state: StateFlow<PlacesState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val mutex = Mutex()
    @Volatile private var area: Area = Area.NEARBY

    init {
        scope.launch {
            preferences.area.distinctUntilChanged().collect { selected ->
                area = selected
                load(selected, force = false)
            }
        }
    }

    /** Re-fetches from the network; also used after location permission is granted. */
    fun refresh() {
        scope.launch { load(area, force = true) }
    }

    fun find(id: String): Restaurant? = (state.value as? PlacesState.Ready)?.restaurants?.firstOrNull { it.id == id }

    private suspend fun load(area: Area, force: Boolean) {
        mutex.withLock { loadLocked(area, force) }
    }

    private suspend fun loadLocked(area: Area, force: Boolean) {
        val ready = _state.value as? PlacesState.Ready
        if (ready?.area == area) _isRefreshing.value = true else _state.value = PlacesState.Loading
        try {
            val center = area.point ?: run {
                if (!location.hasPermission()) {
                    _state.value = PlacesState.NeedsLocation
                    return
                }
                location.current() ?: run {
                    _state.value = PlacesState.Error("Couldn't get your location. Try again or pick a city.")
                    return
                }
            }

            val cached = cache.read()?.takeIf { it.areaId == area.id && GeoPoint.distanceMeters(it.center, center) < SAME_PLACE_M }
            if (!force && cached != null && System.currentTimeMillis() - cached.fetchedAt < CACHE_TTL_MS) {
                _state.value = PlacesState.Ready(area, cached.center, parse(cached.body, cached.center))
                return
            }

            try {
                val body = api.fetchNearby(center, RADIUS_M)
                val restaurants = parse(body, center)
                cache.write(PlacesCache.Entry(area.id, center, System.currentTimeMillis(), body))
                _state.value = PlacesState.Ready(area, center, restaurants)
            } catch (e: Exception) {
                _state.value = when {
                    cached != null -> PlacesState.Ready(area, cached.center, parse(cached.body, cached.center))
                    ready?.area == area -> ready
                    else -> PlacesState.Error(e.message)
                }
            }
        } finally {
            _isRefreshing.value = false
        }
    }

    private suspend fun parse(body: String, center: GeoPoint): List<Restaurant> = withContext(Dispatchers.Default) {
        OverpassApi.parse(body).map { it.withDistanceFrom(center) }.sortedBy { it.distanceMeters }
    }

    private companion object {
        const val RADIUS_M = 3_000
        const val SAME_PLACE_M = 1_000
        const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L
    }
}

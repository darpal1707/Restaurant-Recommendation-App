package com.darpal.foodlabrinthnew.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.PlacesState
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.model.GeoPoint
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import com.darpal.foodlabrinthnew.ui.components.RestaurantListItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = true,
    val needsLocation: Boolean = false,
    val center: GeoPoint? = null,
    val restaurants: List<Restaurant> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
)

class MapViewModel(private val repository: RestaurantRepository, private val preferences: PreferencesRepository) : ViewModel() {
    val uiState: StateFlow<MapUiState> = combine(repository.state, preferences.favoriteIds) { state, favorites ->
        MapUiState(
            isLoading = state is PlacesState.Loading,
            needsLocation = state is PlacesState.NeedsLocation,
            center = (state as? PlacesState.Ready)?.center,
            restaurants = (state as? PlacesState.Ready)?.restaurants.orEmpty(),
            favoriteIds = favorites,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MapUiState())

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) repository.refresh()
    }

    fun toggleFavorite(restaurant: Restaurant) {
        viewModelScope.launch { preferences.toggleFavorite(restaurant) }
    }
}

@Composable
fun MapScreen(
    onOpenRestaurant: (String) -> Unit,
    viewModel: MapViewModel = viewModel(factory = appViewModelFactory { MapViewModel(it.restaurantRepository, it.preferencesRepository) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    fun granted() = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    var hasLocationPermission by remember { mutableStateOf(granted()) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        hasLocationPermission = result.values.any { it }
        viewModel.onLocationPermissionResult(hasLocationPermission)
    }

    // The id is kept after the card is dismissed so it can animate out with its content.
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var showCard by rememberSaveable { mutableStateOf(false) }
    val selected = state.restaurants.firstOrNull { it.id == selectedId }

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(39.5, -98.35), 3f)
    }
    var mapLoaded by remember { mutableStateOf(false) }
    var framedCenter by rememberSaveable { mutableStateOf<String?>(null) }

    // Frame the restaurants whenever the search centre changes, once the map has a size
    // (required by newLatLngBounds).
    LaunchedEffect(mapLoaded, state.center, state.restaurants) {
        val center = state.center ?: return@LaunchedEffect
        val key = "${center.latitude},${center.longitude}"
        if (!mapLoaded || framedCenter == key) return@LaunchedEffect
        val update = if (state.restaurants.isEmpty()) {
            CameraUpdateFactory.newLatLngZoom(LatLng(center.latitude, center.longitude), 14f)
        } else {
            val bounds = LatLngBounds.builder().apply {
                include(LatLng(center.latitude, center.longitude))
                state.restaurants.take(60).forEach { include(LatLng(it.latitude, it.longitude)) }
            }.build()
            CameraUpdateFactory.newLatLngBounds(bounds, 120)
        }
        runCatching { cameraState.animate(update, 900) }
        framedCenter = key
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocationPermission,
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
            ),
            contentPadding = WindowInsets.statusBars.asPaddingValues(),
            onMapLoaded = { mapLoaded = true },
            onMapClick = { showCard = false },
        ) {
            state.restaurants.forEach { restaurant ->
                key(restaurant.id) {
                    Marker(
                        state = rememberMarkerState(position = LatLng(restaurant.latitude, restaurant.longitude)),
                        title = restaurant.name,
                        snippet = restaurant.cityState,
                        onClick = {
                            selectedId = restaurant.id
                            showCard = true
                            false
                        },
                        onInfoWindowClick = { onOpenRestaurant(restaurant.id) },
                    )
                }
            }
        }

        if (state.isLoading) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 3.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(16.dp),
            ) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.map_loading), style = MaterialTheme.typography.labelLarge)
                }
            }
        } else if (state.needsLocation || !hasLocationPermission) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 3.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(16.dp).fillMaxWidth(),
            ) {
                Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.map_permission_rationale), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    FilledTonalButton(onClick = {
                        permissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        )
                    }) { Text(stringResource(R.string.map_permission_action)) }
                }
            }
        }

        // OSM data attribution (ODbL); lifted above the preview card when it is showing.
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = if (showCard && selected != null) 136.dp else 8.dp),
        ) {
            Text(
                stringResource(R.string.osm_attribution),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        AnimatedVisibility(
            visible = showCard && selected != null,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) {
            selected?.let { restaurant ->
                RestaurantListItem(
                    restaurant = restaurant,
                    isFavorite = restaurant.id in state.favoriteIds,
                    onClick = { onOpenRestaurant(restaurant.id) },
                    onToggleFavorite = { viewModel.toggleFavorite(restaurant) },
                )
            }
        }
    }
}

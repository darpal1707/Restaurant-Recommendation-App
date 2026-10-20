package com.darpal.foodlabrinthnew.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.darpal.foodlabrinthnew.ui.components.frame
import com.darpal.foodlabrinthnew.ui.components.rememberOsmMapView
import com.darpal.foodlabrinthnew.ui.components.toOsm
import com.darpal.foodlabrinthnew.ui.theme.LocalReducedMotion
import com.darpal.foodlabrinthnew.ui.theme.Motion
import com.darpal.foodlabrinthnew.ui.theme.MotionMath
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

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

    val mapView = rememberOsmMapView(interactive = true)
    val markers = remember { mutableMapOf<String, Marker>() }
    val pin = remember { ContextCompat.getDrawable(context, R.drawable.ic_map_pin) }
    val selectedPin = remember { ContextCompat.getDrawable(context, R.drawable.ic_map_pin_selected) }
    val onMarkerTap by rememberUpdatedState { id: String ->
        selectedId = id
        showCard = true
    }

    // Tapping empty map dismisses the preview card.
    DisposableEffect(mapView) {
        val events = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: org.osmdroid.util.GeoPoint?): Boolean {
                showCard = false
                return false
            }

            override fun longPressHelper(p: org.osmdroid.util.GeoPoint?): Boolean = false
        })
        mapView.overlays.add(0, events)
        onDispose { mapView.overlays.remove(events) }
    }

    // Rebuild markers when the restaurant list changes.
    LaunchedEffect(mapView, state.restaurants) {
        markers.values.forEach { mapView.overlays.remove(it) }
        markers.clear()
        state.restaurants.forEach { restaurant ->
            val marker = Marker(mapView).apply {
                position = restaurant.location.toOsm()
                title = restaurant.name
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                setIcon(if (restaurant.id == selectedId) selectedPin else pin)
                setOnMarkerClickListener { _, _ ->
                    onMarkerTap(restaurant.id)
                    true
                }
            }
            markers[restaurant.id] = marker
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // Highlight the selected marker.
    LaunchedEffect(selectedId, showCard, state.restaurants) {
        markers.forEach { (id, marker) -> marker.setIcon(if (showCard && id == selectedId) selectedPin else pin) }
        mapView.invalidate()
    }

    // Frame the results whenever the search centre changes.
    var framedCenter by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(state.center, state.restaurants) {
        val center = state.center ?: return@LaunchedEffect
        val key = "${center.latitude},${center.longitude}"
        if (framedCenter == key) return@LaunchedEffect
        mapView.frame(center, state.restaurants.take(60).map { it.location })
        framedCenter = key
    }

    // "My location" dot, only once permission is granted.
    DisposableEffect(mapView, hasLocationPermission) {
        if (!hasLocationPermission) return@DisposableEffect onDispose { }
        val overlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        overlay.enableMyLocation()
        mapView.overlays.add(overlay)
        onDispose {
            overlay.disableMyLocation()
            mapView.overlays.remove(overlay)
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        Column(Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(16.dp)) {
            if (state.isLoading) {
                Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 3.dp, shadowElevation = 4.dp) {
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
                    modifier = Modifier.fillMaxWidth(),
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
        }

        val cardShowing = showCard && selected != null
        Column(
            Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = if (cardShowing) 140.dp else 12.dp),
            horizontalAlignment = Alignment.End,
        ) {
            state.center?.let { center ->
                SmallFloatingActionButton(onClick = { mapView.frame(center, state.restaurants.take(60).map { it.location }) }) {
                    Icon(Icons.Rounded.CenterFocusStrong, contentDescription = stringResource(R.string.map_recenter))
                }
                Spacer(Modifier.size(8.dp))
            }
            // Tile and data attribution (ODbL).
            Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)) {
                Text(
                    stringResource(R.string.osm_attribution),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }

        val reducedMotion = LocalReducedMotion.current
        val cardSpec = Motion.spring(visibilityThreshold = IntOffset.VisibilityThreshold)
        AnimatedVisibility(
            visible = cardShowing,
            // Rises from the bottom edge and leaves the same way.
            enter = if (reducedMotion) fadeIn(Motion.fade()) else slideInVertically(cardSpec) { it },
            exit = if (reducedMotion) fadeOut(Motion.fade()) else slideOutVertically(cardSpec) { it },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) {
            selected?.let { restaurant ->
                SwipeDownToDismiss(visible = cardShowing, onDismiss = { showCard = false }) {
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
}

/**
 * Lets the preview card be flicked down and away. The card tracks the finger 1:1 from wherever it
 * was grabbed (including mid-animation), resists progressively when pulled up past its resting
 * place, and on release projects the flick's momentum to decide whether to dismiss or settle back,
 * handing the finger's velocity to the spring so there's no seam between drag and animation.
 */
@Composable
private fun SwipeDownToDismiss(visible: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val cardOffset = remember { Animatable(0f) }
    var height by remember { mutableFloatStateOf(0f) }
    // Finger travel since the drag began; the shown offset is derived from it.
    var dragged by remember { mutableFloatStateOf(0f) }
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    // Shown again (e.g. another pin tapped while the card was leaving): return to rest.
    LaunchedEffect(visible) {
        if (visible && cardOffset.value != 0f) cardOffset.animateTo(0f, Motion.spring())
    }

    Box(
        Modifier
            .onSizeChanged { height = it.height.toFloat() }
            .offset { IntOffset(0, cardOffset.value.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    dragged += delta
                    val shown = if (dragged < 0f) MotionMath.rubberBand(dragged, height) else dragged
                    scope.launch { cardOffset.snapTo(shown) }
                },
                onDragStarted = { dragged = cardOffset.value },
                onDragStopped = { velocity ->
                    val restingPoint = cardOffset.value + MotionMath.project(velocity)
                    if (restingPoint > height / 2) {
                        cardOffset.animateTo(height * 1.5f, Motion.spring(), initialVelocity = velocity)
                        currentOnDismiss()
                    } else {
                        // Settling back after a throw may overshoot a little; it carried momentum.
                        cardOffset.animateTo(0f, Motion.spring(Motion.Momentum), initialVelocity = velocity)
                    }
                },
            ),
    ) {
        content()
    }
}

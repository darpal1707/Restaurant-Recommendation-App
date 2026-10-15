package com.darpal.foodlabrinthnew.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.darpal.foodlabrinthnew.data.model.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.util.GeoPoint as OsmPoint

/**
 * An osmdroid [MapView] showing OpenStreetMap tiles (no API key), tied to the composition's
 * lifecycle. With [interactive] false the map ignores touches, for small preview maps.
 *
 * Tiles come from tile.openstreetmap.org, which is free for light use under the OSM tile usage
 * policy; osmdroid sends the app's package name as User-Agent as that policy requires.
 */
@SuppressLint("ClickableViewAccessibility")
@Composable
fun rememberOsmMapView(interactive: Boolean): MapView {
    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setTilesScaledToDpi(true)
            setMultiTouchControls(interactive)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            setMinZoomLevel(3.0)
            isVerticalMapRepetitionEnabled = false
            if (!interactive) setOnTouchListener { _, _ -> true }
            // OSM has no dark tiles; inverting keeps the map from glaring in dark mode.
            if (dark) overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
            controller.setZoom(3.0)
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }
    return mapView
}

fun GeoPoint.toOsm(): OsmPoint = OsmPoint(latitude, longitude)

/** Centres on [center], zoomed out just enough to show [points]; waits for the first layout if needed. */
fun MapView.frame(center: GeoPoint, points: List<GeoPoint>, borderPx: Int = 96) {
    val apply = {
        if (points.isEmpty()) {
            controller.setZoom(15.0)
            controller.setCenter(center.toOsm())
        } else {
            val box = BoundingBox.fromGeoPoints((points + center).map { it.toOsm() })
            zoomToBoundingBox(box, false, borderPx)
            if (zoomLevelDouble > 17.0) controller.setZoom(17.0)
        }
    }
    if (width > 0 && height > 0) apply() else addOnFirstLayoutListener { _, _, _, _, _ -> apply() }
}

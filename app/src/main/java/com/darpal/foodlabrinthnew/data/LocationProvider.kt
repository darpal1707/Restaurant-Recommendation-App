package com.darpal.foodlabrinthnew.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import com.darpal.foodlabrinthnew.data.model.GeoPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/** Device location through the platform LocationManager (no Google Play services needed). */
class LocationProvider(context: Context) {
    private val app = context.applicationContext
    private val manager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun hasPermission(): Boolean =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(app, it) == PackageManager.PERMISSION_GRANTED }

    /** A recent last-known fix, otherwise a fresh one (up to ~15 s); null if unavailable. */
    @SuppressLint("MissingPermission")
    suspend fun current(): GeoPoint? {
        if (!hasPermission()) return null
        val providers = runCatching { manager.getProviders(true) }.getOrDefault(emptyList())
        val last = providers
            .mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        if (last != null && System.currentTimeMillis() - last.time < MAX_AGE_MS) return last.toGeoPoint()

        val provider = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            .firstOrNull { it in providers }
            ?: return last?.toGeoPoint()
        val fresh = withTimeoutOrNull(FIX_TIMEOUT_MS) { requestSingleFix(provider) }
        return (fresh ?: last)?.toGeoPoint()
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleFix(provider: String): Location? = suspendCancellableCoroutine { cont ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val signal = CancellationSignal()
            manager.getCurrentLocation(provider, signal, ContextCompat.getMainExecutor(app)) { cont.resume(it) }
            cont.invokeOnCancellation { signal.cancel() }
        } else {
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    manager.removeUpdates(this)
                    if (cont.isActive) cont.resume(location)
                }

                override fun onProviderEnabled(provider: String) = Unit
                override fun onProviderDisabled(provider: String) = Unit

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            }
            @Suppress("DEPRECATION")
            manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            cont.invokeOnCancellation { manager.removeUpdates(listener) }
        }
    }

    private fun Location.toGeoPoint() = GeoPoint(latitude, longitude)

    private companion object {
        const val MAX_AGE_MS = 30 * 60 * 1000L
        const val FIX_TIMEOUT_MS = 15_000L
    }
}

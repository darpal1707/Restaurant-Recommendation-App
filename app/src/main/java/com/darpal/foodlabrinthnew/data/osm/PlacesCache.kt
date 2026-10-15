package com.darpal.foodlabrinthnew.data.osm

import android.content.Context
import com.darpal.foodlabrinthnew.data.model.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

/** The last Overpass response, stored on disk so the app opens instantly and works offline. */
class PlacesCache(context: Context) {
    private val file = File(context.applicationContext.filesDir, "places_cache.json")

    data class Entry(val areaId: String, val center: GeoPoint, val fetchedAt: Long, val body: String)

    suspend fun read(): Entry? = withContext(Dispatchers.IO) {
        runCatching {
            if (!file.exists()) return@runCatching null
            val json = JSONObject(file.readText())
            Entry(
                areaId = json.getString("areaId"),
                center = GeoPoint(json.getDouble("lat"), json.getDouble("lon")),
                fetchedAt = json.getLong("fetchedAt"),
                body = json.getString("body"),
            )
        }.getOrNull()
    }

    suspend fun write(entry: Entry) = withContext(Dispatchers.IO) {
        runCatching {
            val json = JSONObject()
                .put("areaId", entry.areaId)
                .put("lat", entry.center.latitude)
                .put("lon", entry.center.longitude)
                .put("fetchedAt", entry.fetchedAt)
                .put("body", entry.body)
            file.writeText(json.toString())
        }
        Unit
    }
}

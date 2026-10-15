package com.darpal.foodlabrinthnew.data.osm

import com.darpal.foodlabrinthnew.data.model.GeoPoint
import com.darpal.foodlabrinthnew.data.model.OsmParser
import com.darpal.foodlabrinthnew.data.model.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

/**
 * Minimal client for the public Overpass API (https://wiki.openstreetmap.org/wiki/Overpass_API).
 * The public instance is free but rate limited, so results are cached by the repository.
 */
class OverpassApi(private val endpoint: String = DEFAULT_ENDPOINT) {

    /** Returns the raw JSON response for eating places within [radiusMeters] of [center]. */
    suspend fun fetchNearby(center: GeoPoint, radiusMeters: Int): String = withContext(Dispatchers.IO) {
        val query = String.format(
            Locale.US,
            """[out:json][timeout:25];nwr["amenity"~"^(restaurant|cafe|fast_food)$"]["name"](around:%d,%.6f,%.6f);out center tags %d;""",
            radiusMeters, center.latitude, center.longitude, MAX_RESULTS,
        )
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15_000
            readTimeout = 40_000
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
            // The Overpass usage policy asks clients to identify themselves.
            setRequestProperty("User-Agent", "FoodLabrinth/2.0 (Android app)")
        }
        try {
            connection.outputStream.use { it.write(("data=" + URLEncoder.encode(query, "UTF-8")).toByteArray()) }
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) throw IOException("Overpass returned HTTP $code")
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        const val DEFAULT_ENDPOINT = "https://overpass-api.de/api/interpreter"
        private const val MAX_RESULTS = 400

        /** Parses an Overpass JSON response into restaurants, skipping unusable elements. */
        fun parse(json: String): List<Restaurant> {
            val elements = JSONObject(json).optJSONArray("elements") ?: return emptyList()
            return (0 until elements.length()).mapNotNull { i ->
                val element = elements.optJSONObject(i) ?: return@mapNotNull null
                val center = element.optJSONObject("center")
                val tagsJson = element.optJSONObject("tags") ?: return@mapNotNull null
                val tags = tagsJson.keys().asSequence().associateWith { tagsJson.optString(it) }
                OsmParser.toRestaurant(
                    type = element.optString("type"),
                    id = element.optLong("id"),
                    latitude = element.optNullableDouble("lat") ?: center?.optNullableDouble("lat"),
                    longitude = element.optNullableDouble("lon") ?: center?.optNullableDouble("lon"),
                    tags = tags,
                )
            }.distinctBy { it.id }
        }

        private fun JSONObject.optNullableDouble(key: String): Double? =
            if (has(key)) optDouble(key).takeUnless { it.isNaN() } else null
    }
}

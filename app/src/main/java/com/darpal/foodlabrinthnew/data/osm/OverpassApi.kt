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
import java.net.UnknownHostException
import java.util.Locale

/** Every Overpass server failed or was busy (HTTP 429/504, timeouts, query errors). */
class OverpassUnavailableException(cause: Throwable?) : IOException("All Overpass servers failed", cause)

/**
 * Minimal client for the public Overpass API (https://wiki.openstreetmap.org/wiki/Overpass_API).
 *
 * The public servers are free but often overloaded (HTTP 504/429), so requests fall back through
 * several independent instances. Results are cached by the repository to keep traffic low.
 */
class OverpassApi(private val endpoints: List<String> = DEFAULT_ENDPOINTS) {

    /** Returns the raw JSON response for eating places within [radiusMeters] of [center]. */
    suspend fun fetchNearby(center: GeoPoint, radiusMeters: Int): String = withContext(Dispatchers.IO) {
        val query = buildQuery(center, radiusMeters)
        var lastError: Exception? = null
        for (endpoint in endpoints) {
            try {
                val body = post(endpoint, query)
                val remark = errorRemark(body)
                if (remark != null) throw IOException("Overpass error at $endpoint: $remark")
                return@withContext body
            } catch (e: UnknownHostException) {
                // No network at all: trying other servers won't help.
                throw e
            } catch (e: IOException) {
                lastError = e
            }
        }
        throw OverpassUnavailableException(lastError)
    }

    private fun post(endpoint: String, query: String): String {
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 10_000
            readTimeout = 30_000
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
            // The Overpass usage policy asks clients to identify themselves.
            setRequestProperty("User-Agent", "FoodLabrinth/2.0 (Android app)")
        }
        try {
            connection.outputStream.use { it.write(("data=" + URLEncoder.encode(query, "UTF-8")).toByteArray()) }
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) throw IOException("Overpass $endpoint returned HTTP $code")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        /** Public instances listed on the OSM wiki; tried in order. */
        val DEFAULT_ENDPOINTS = listOf(
            "https://overpass-api.de/api/interpreter",
            "https://overpass.kumi.systems/api/interpreter",
            "https://overpass.private.coffee/api/interpreter",
            "https://maps.mail.ru/osm/tools/overpass/api/interpreter",
        )
        private const val MAX_RESULTS = 400

        fun buildQuery(center: GeoPoint, radiusMeters: Int): String = String.format(
            Locale.US,
            """[out:json][timeout:25];nwr["amenity"~"^(restaurant|cafe|fast_food)$"]["name"](around:%d,%.6f,%.6f);out center tags %d;""",
            radiusMeters, center.latitude, center.longitude, MAX_RESULTS,
        )

        /**
         * Overpass reports timeouts and memory limits as HTTP 200 with a `remark` and no elements.
         * Returns that remark, or null for a normal response.
         */
        fun errorRemark(json: String): String? = runCatching {
            val obj = JSONObject(json)
            val remark = obj.optString("remark")
            val noElements = (obj.optJSONArray("elements")?.length() ?: 0) == 0
            remark.takeIf { noElements && it.contains("error", ignoreCase = true) }
        }.getOrElse { "Invalid response" }

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

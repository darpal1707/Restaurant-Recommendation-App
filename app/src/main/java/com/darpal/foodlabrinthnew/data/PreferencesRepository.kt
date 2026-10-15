package com.darpal.foodlabrinthnew.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.darpal.foodlabrinthnew.data.model.Area
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.data.model.PlaceKind
import com.darpal.foodlabrinthnew.data.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "food_labrinth")

/**
 * On-device preferences: selected area, saved restaurants and liked cuisines (which drive the
 * "Picked for you" section). Saved restaurants are stored as full snapshots so they stay visible
 * after switching to another area.
 */
class PreferencesRepository(context: Context) {
    private val store = context.applicationContext.dataStore

    val area: Flow<Area> = store.data.map { Area.byId(it[AREA]) }
    val likedCuisines: Flow<Set<String>> = store.data.map { it[LIKED_CUISINES].orEmpty() }
    val savedRestaurants: Flow<List<Restaurant>> = store.data.map { prefs ->
        prefs[SAVED].orEmpty().mapNotNull { runCatching { fromJson(JSONObject(it)) }.getOrNull() }.sortedBy { it.name }
    }
    val favoriteIds: Flow<Set<String>> = savedRestaurants.map { list -> list.map { it.id }.toSet() }

    suspend fun setArea(area: Area) {
        store.edit { it[AREA] = area.id }
    }

    /** Saves or un-saves a restaurant. Saving also likes its cuisines. Returns true when saved. */
    suspend fun toggleFavorite(restaurant: Restaurant): Boolean {
        var saved = false
        store.edit { prefs ->
            val current = prefs[SAVED].orEmpty()
            val existing = current.filter { idOf(it) == restaurant.id }.toSet()
            if (existing.isNotEmpty()) {
                prefs[SAVED] = current - existing
            } else {
                saved = true
                prefs[SAVED] = current + toJson(restaurant).toString()
                prefs[LIKED_CUISINES] = prefs[LIKED_CUISINES].orEmpty() + Cuisine.matching(restaurant).map { it.name }
            }
        }
        return saved
    }

    suspend fun removeFavorite(id: String) {
        store.edit { prefs -> prefs[SAVED] = prefs[SAVED].orEmpty().filterNot { idOf(it) == id }.toSet() }
    }

    suspend fun setCuisineLiked(name: String, liked: Boolean) {
        store.edit {
            val current = it[LIKED_CUISINES].orEmpty()
            it[LIKED_CUISINES] = if (liked) current + name else current - name
        }
    }

    private fun idOf(json: String): String? = runCatching { JSONObject(json).getString("id") }.getOrNull()

    private fun toJson(r: Restaurant): JSONObject = JSONObject()
        .put("id", r.id)
        .put("name", r.name)
        .put("lat", r.latitude)
        .put("lon", r.longitude)
        .put("kind", r.kind.name)
        .put("categories", JSONArray(r.categories))
        .put("address", r.address)
        .put("city", r.city)
        .put("state", r.state)
        .put("postalCode", r.postalCode)
        .put("openingHours", r.openingHours ?: JSONObject.NULL)
        .put("phone", r.phone ?: JSONObject.NULL)
        .put("website", r.website ?: JSONObject.NULL)
        .put("features", JSONArray(r.features))

    private fun fromJson(o: JSONObject): Restaurant = Restaurant(
        id = o.getString("id"),
        name = o.getString("name"),
        latitude = o.getDouble("lat"),
        longitude = o.getDouble("lon"),
        kind = runCatching { PlaceKind.valueOf(o.getString("kind")) }.getOrDefault(PlaceKind.RESTAURANT),
        categories = o.optJSONArray("categories").toStrings(),
        address = o.optString("address"),
        city = o.optString("city"),
        state = o.optString("state"),
        postalCode = o.optString("postalCode"),
        openingHours = o.optStringOrNull("openingHours"),
        phone = o.optStringOrNull("phone"),
        website = o.optStringOrNull("website"),
        features = o.optJSONArray("features").toStrings(),
    )

    private fun JSONArray?.toStrings(): List<String> = if (this == null) emptyList() else (0 until length()).map { getString(it) }

    private fun JSONObject.optStringOrNull(key: String): String? = if (isNull(key)) null else optString(key).ifEmpty { null }

    private companion object {
        val AREA = stringPreferencesKey("area")
        val SAVED = stringSetPreferencesKey("saved_restaurants")
        val LIKED_CUISINES = stringSetPreferencesKey("liked_cuisines")
    }
}

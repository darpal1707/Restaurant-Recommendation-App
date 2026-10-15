package com.darpal.foodlabrinthnew.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.data.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "food_labrinth")

/**
 * On-device preferences: saved restaurants and liked cuisines (which drive the "Picked for you"
 * section). Kept locally, so recommendations work without signing in.
 */
class PreferencesRepository(context: Context) {
    private val store = context.applicationContext.dataStore

    val favoriteIds: Flow<Set<String>> = store.data.map { it[FAVORITES].orEmpty() }
    val likedCuisines: Flow<Set<String>> = store.data.map { it[LIKED_CUISINES].orEmpty() }

    /** Saves or un-saves a restaurant. Saving also likes its cuisines. Returns true when saved. */
    suspend fun toggleFavorite(restaurant: Restaurant): Boolean {
        var saved = false
        store.edit { prefs ->
            val favorites = prefs[FAVORITES].orEmpty()
            if (restaurant.id in favorites) {
                prefs[FAVORITES] = favorites - restaurant.id
            } else {
                saved = true
                prefs[FAVORITES] = favorites + restaurant.id
                prefs[LIKED_CUISINES] = prefs[LIKED_CUISINES].orEmpty() + Cuisine.matching(restaurant).map { it.name }
            }
        }
        return saved
    }

    suspend fun removeFavorite(id: String) {
        store.edit { it[FAVORITES] = it[FAVORITES].orEmpty() - id }
    }

    suspend fun setCuisineLiked(name: String, liked: Boolean) {
        store.edit {
            val current = it[LIKED_CUISINES].orEmpty()
            it[LIKED_CUISINES] = if (liked) current + name else current - name
        }
    }

    private companion object {
        val FAVORITES = stringSetPreferencesKey("favorite_ids")
        val LIKED_CUISINES = stringSetPreferencesKey("liked_cuisines")
    }
}

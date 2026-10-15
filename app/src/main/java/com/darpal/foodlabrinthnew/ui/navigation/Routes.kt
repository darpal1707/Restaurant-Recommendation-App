package com.darpal.foodlabrinthnew.ui.navigation

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.FeatureFlags
import com.darpal.foodlabrinthnew.data.model.Distance
import com.darpal.foodlabrinthnew.data.model.Meal

object Routes {
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val MAP = "map"
    const val SAVED = "saved"
    const val PROFILE = "profile"
    const val DECIDE = "decide"

    const val ARG_ID = "id"
    const val DETAIL = "restaurant/{$ARG_ID}"
    fun detail(id: String) = "restaurant/${Uri.encode(id)}"

    const val ARG_TITLE = "title"
    const val ARG_QUERY = "query"
    const val ARG_CUISINE = "cuisine"
    const val ARG_CUISINES = "cuisines"
    const val ARG_MEAL = "meal"
    const val ARG_DISTANCE = "distance"
    val RESULTS_ARGS = listOf(ARG_TITLE, ARG_QUERY, ARG_CUISINE, ARG_CUISINES, ARG_MEAL, ARG_DISTANCE)
    val RESULTS = "results?" + RESULTS_ARGS.joinToString("&") { "$it={$it}" }

    const val CUISINE_SEPARATOR = "|"

    fun search(query: String) = results(ARG_QUERY to query)
    fun cuisine(name: String) = results(ARG_CUISINE to name)
    fun cuisines(title: String, names: Collection<String>) =
        results(ARG_TITLE to title, ARG_CUISINES to names.joinToString(CUISINE_SEPARATOR))

    fun allNearby(title: String) = results(ARG_TITLE to title)

    fun decideResults(meal: Meal?, cuisines: Collection<String>, distance: Distance?) = results(
        ARG_CUISINES to cuisines.joinToString(CUISINE_SEPARATOR),
        ARG_MEAL to meal?.name,
        ARG_DISTANCE to distance?.name,
    )

    private fun results(vararg args: Pair<String, String?>): String =
        "results?" + args.filter { it.second != null }.joinToString("&") { (k, v) -> "$k=${Uri.encode(v)}" }
}

enum class TopLevelDestination(
    val route: String,
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(Routes.HOME, R.string.nav_home, Icons.Rounded.Home, Icons.Outlined.Home),
    EXPLORE(Routes.EXPLORE, R.string.nav_explore, Icons.Rounded.Explore, Icons.Outlined.Explore),
    MAP(Routes.MAP, R.string.nav_map, Icons.Rounded.Map, Icons.Outlined.Map),
    SAVED(Routes.SAVED, R.string.nav_saved, Icons.Rounded.Favorite, Icons.Outlined.FavoriteBorder),
    PROFILE(Routes.PROFILE, R.string.nav_profile, Icons.Rounded.Person, Icons.Outlined.Person),
    ;

    companion object {
        /** Tabs shown in the bottom bar; Profile (sign-in) is hidden while accounts are disabled. */
        val visible: List<TopLevelDestination> =
            entries.filter { it != PROFILE || FeatureFlags.ACCOUNTS_ENABLED }
    }
}

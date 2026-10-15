package com.darpal.foodlabrinthnew.ui.results

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darpal.foodlabrinthnew.data.PlacesState
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.data.model.Distance
import com.darpal.foodlabrinthnew.data.model.Meal
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.data.model.RestaurantFilters
import com.darpal.foodlabrinthnew.ui.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ResultsMode { SEARCH, CUISINE, DECIDE }

data class ResultsUiState(
    val isLoading: Boolean = true,
    @StringRes val error: Int? = null,
    val needsLocation: Boolean = false,
    val results: List<Restaurant> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
)

class ResultsViewModel(
    savedState: SavedStateHandle,
    private val restaurantRepository: RestaurantRepository,
    private val preferences: PreferencesRepository,
) : ViewModel() {

    val customTitle: String? = savedState.get<String>(Routes.ARG_TITLE)
    val cuisine: Cuisine? = savedState.get<String>(Routes.ARG_CUISINE)?.let(Cuisine::byName)
    val cuisines: List<Cuisine> = savedState.get<String>(Routes.ARG_CUISINES)
        ?.split(Routes.CUISINE_SEPARATOR)
        ?.mapNotNull(Cuisine::byName)
        .orEmpty()
    private val meal = savedState.get<String>(Routes.ARG_MEAL)?.let { runCatching { Meal.valueOf(it) }.getOrNull() }
    private val distance = savedState.get<String>(Routes.ARG_DISTANCE)?.let { runCatching { Distance.valueOf(it) }.getOrNull() }

    val mode: ResultsMode = when {
        cuisines.isNotEmpty() -> ResultsMode.DECIDE
        cuisine != null -> ResultsMode.CUISINE
        else -> ResultsMode.SEARCH
    }

    private val _query = MutableStateFlow(savedState.get<String>(Routes.ARG_QUERY).orEmpty())
    val query: StateFlow<String> = _query.asStateFlow()

    val uiState: StateFlow<ResultsUiState> = combine(
        restaurantRepository.state,
        _query,
        preferences.favoriteIds,
    ) { state, query, favorites ->
        when (state) {
            PlacesState.Loading -> ResultsUiState(isLoading = true)
            PlacesState.NeedsLocation -> ResultsUiState(isLoading = false, needsLocation = true)
            is PlacesState.Error -> ResultsUiState(isLoading = false, error = state.message)
            is PlacesState.Ready -> ResultsUiState(
                isLoading = false,
                results = filter(state.restaurants, query),
                favoriteIds = favorites,
            )
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultsUiState())

    private fun filter(all: List<Restaurant>, query: String): List<Restaurant> = when (mode) {
        ResultsMode.DECIDE -> RestaurantFilters.decide(all, cuisines, meal, distance)
        ResultsMode.CUISINE -> all.filter { cuisine!!.matches(it) && RestaurantFilters.matchesQuery(it, query) }
            .sortedWith(RestaurantFilters.byDistance())
        ResultsMode.SEARCH -> all.filter { RestaurantFilters.matchesQuery(it, query) }
            .sortedWith(RestaurantFilters.byDistance())
    }

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun retry() = restaurantRepository.refresh()

    fun toggleFavorite(restaurant: Restaurant) {
        viewModelScope.launch { preferences.toggleFavorite(restaurant) }
    }
}

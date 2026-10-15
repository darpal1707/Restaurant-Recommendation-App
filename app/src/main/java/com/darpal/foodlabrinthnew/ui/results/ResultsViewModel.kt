package com.darpal.foodlabrinthnew.ui.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.Resource
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.model.Ambience
import com.darpal.foodlabrinthnew.data.model.Budget
import com.darpal.foodlabrinthnew.data.model.Cuisine
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
    val error: String? = null,
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
    private val ambience = savedState.get<String>(Routes.ARG_AMBIENCE)?.let { runCatching { Ambience.valueOf(it) }.getOrNull() }
    private val budget = savedState.get<String>(Routes.ARG_BUDGET)?.let { runCatching { Budget.valueOf(it) }.getOrNull() }

    val mode: ResultsMode = when {
        cuisines.isNotEmpty() -> ResultsMode.DECIDE
        cuisine != null -> ResultsMode.CUISINE
        else -> ResultsMode.SEARCH
    }

    private val _query = MutableStateFlow(savedState.get<String>(Routes.ARG_QUERY).orEmpty())
    val query: StateFlow<String> = _query.asStateFlow()

    val uiState: StateFlow<ResultsUiState> = combine(
        restaurantRepository.restaurants,
        _query,
        preferences.favoriteIds,
    ) { resource, query, favorites ->
        when (resource) {
            Resource.Loading -> ResultsUiState(isLoading = true)
            is Resource.Error -> ResultsUiState(isLoading = false, error = resource.message)
            is Resource.Success -> ResultsUiState(
                isLoading = false,
                results = filter(resource.data, query),
                favoriteIds = favorites,
            )
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultsUiState())

    private fun filter(all: List<Restaurant>, query: String): List<Restaurant> = when (mode) {
        ResultsMode.DECIDE -> RestaurantFilters.decide(all, cuisines, meal, ambience, budget)
        ResultsMode.CUISINE -> all.filter { cuisine!!.matches(it) && RestaurantFilters.matchesQuery(it, query) }
            .sortedWith(RestaurantFilters.byRating())
        ResultsMode.SEARCH -> all.filter { RestaurantFilters.matchesQuery(it, query) }
            .sortedWith(RestaurantFilters.byRating())
    }

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun retry() = restaurantRepository.refresh()

    fun toggleFavorite(restaurant: Restaurant) {
        viewModelScope.launch { preferences.toggleFavorite(restaurant) }
    }
}

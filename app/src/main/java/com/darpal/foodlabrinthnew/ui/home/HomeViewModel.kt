package com.darpal.foodlabrinthnew.ui.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darpal.foodlabrinthnew.data.PlacesState
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.model.Area
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.data.model.RestaurantFilters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val area: Area = Area.NEARBY,
    val isLoading: Boolean = true,
    val needsLocation: Boolean = false,
    @StringRes val error: Int? = null,
    val isRefreshing: Boolean = false,
    val closest: List<Restaurant> = emptyList(),
    val forYou: List<Restaurant> = emptyList(),
    val more: List<Restaurant> = emptyList(),
    val totalCount: Int = 0,
    val likedCuisines: Set<String> = emptySet(),
    val favoriteIds: Set<String> = emptySet(),
)

class HomeViewModel(
    private val restaurantRepository: RestaurantRepository,
    private val preferences: PreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        restaurantRepository.state,
        restaurantRepository.isRefreshing,
        preferences.area,
        preferences.likedCuisines,
        preferences.favoriteIds,
    ) { state, refreshing, area, liked, favorites ->
        val base = HomeUiState(area = area, isRefreshing = refreshing, likedCuisines = liked, favoriteIds = favorites)
        when (state) {
            PlacesState.Loading -> base.copy(isLoading = true)
            PlacesState.NeedsLocation -> base.copy(isLoading = false, needsLocation = true)
            is PlacesState.Error -> base.copy(isLoading = false, error = state.message)
            is PlacesState.Ready -> {
                val all = state.restaurants
                val closest = RestaurantFilters.nearest(all, limit = 10)
                base.copy(
                    isLoading = false,
                    closest = closest,
                    forYou = RestaurantFilters.recommended(all, liked).take(6),
                    more = RestaurantFilters.nearest(all, limit = 25).drop(closest.size),
                    totalCount = all.size,
                )
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun refresh() = restaurantRepository.refresh()

    fun selectArea(area: Area) {
        viewModelScope.launch { preferences.setArea(area) }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) restaurantRepository.refresh()
    }

    fun toggleFavorite(restaurant: Restaurant) {
        viewModelScope.launch { preferences.toggleFavorite(restaurant) }
    }
}

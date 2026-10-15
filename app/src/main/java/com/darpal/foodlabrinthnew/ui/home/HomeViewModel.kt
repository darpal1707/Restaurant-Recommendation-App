package com.darpal.foodlabrinthnew.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darpal.foodlabrinthnew.data.AuthRepository
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.Resource
import com.darpal.foodlabrinthnew.data.RestaurantRepository
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
    val isLoading: Boolean = true,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val userName: String? = null,
    val trending: List<Restaurant> = emptyList(),
    val forYou: List<Restaurant> = emptyList(),
    val topRated: List<Restaurant> = emptyList(),
    val likedCuisines: Set<String> = emptySet(),
    val favoriteIds: Set<String> = emptySet(),
)

class HomeViewModel(
    private val restaurantRepository: RestaurantRepository,
    private val preferences: PreferencesRepository,
    authRepository: AuthRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        restaurantRepository.restaurants,
        restaurantRepository.isRefreshing,
        preferences.likedCuisines,
        preferences.favoriteIds,
        authRepository.user,
    ) { resource, refreshing, liked, favorites, user ->
        when (resource) {
            Resource.Loading -> HomeUiState(isLoading = true, userName = user?.label)
            is Resource.Error -> HomeUiState(isLoading = false, error = resource.message, userName = user?.label)
            is Resource.Success -> {
                val all = resource.data
                HomeUiState(
                    isLoading = false,
                    isRefreshing = refreshing,
                    userName = user?.label,
                    trending = RestaurantFilters.trending(all),
                    forYou = RestaurantFilters.recommended(all, liked).take(6),
                    topRated = RestaurantFilters.topRated(all, limit = 8),
                    likedCuisines = liked,
                    favoriteIds = favorites,
                )
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun refresh() = restaurantRepository.refresh()

    fun toggleFavorite(restaurant: Restaurant) {
        viewModelScope.launch { preferences.toggleFavorite(restaurant) }
    }
}

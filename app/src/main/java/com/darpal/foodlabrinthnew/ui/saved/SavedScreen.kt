package com.darpal.foodlabrinthnew.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.PlacesState
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import com.darpal.foodlabrinthnew.ui.components.EmptyState
import com.darpal.foodlabrinthnew.ui.components.ListItemPlaceholder
import com.darpal.foodlabrinthnew.ui.components.RestaurantListItem
import com.darpal.foodlabrinthnew.ui.components.SectionHeader
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SavedUiState(
    val isLoading: Boolean = true,
    val saved: List<Restaurant> = emptyList(),
    val likedCuisines: Set<String> = emptySet(),
)

class SavedViewModel(restaurants: RestaurantRepository, private val preferences: PreferencesRepository) : ViewModel() {
    val uiState: StateFlow<SavedUiState> = combine(
        restaurants.state,
        preferences.savedRestaurants,
        preferences.likedCuisines,
    ) { state, saved, liked ->
        // Snapshots are stored without distance; add it when we know where the user is looking.
        val center = (state as? PlacesState.Ready)?.center
        SavedUiState(
            isLoading = false,
            saved = if (center == null) saved else saved.map { it.withDistanceFrom(center) },
            likedCuisines = liked,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SavedUiState())

    fun remove(restaurant: Restaurant) {
        viewModelScope.launch { preferences.removeFavorite(restaurant.id) }
    }

    fun setCuisineLiked(name: String, liked: Boolean) {
        viewModelScope.launch { preferences.setCuisineLiked(name, liked) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SavedScreen(
    onOpenRestaurant: (String) -> Unit,
    onExplore: () -> Unit,
    viewModel: SavedViewModel = viewModel(factory = appViewModelFactory { SavedViewModel(it.restaurantRepository, it.preferencesRepository) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.statusBarsPadding().padding(top = 16.dp)) {
                Text(stringResource(R.string.saved_title), style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                SectionHeader(stringResource(R.string.saved_cuisines))
                Text(
                    stringResource(R.string.saved_cuisines_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Cuisine.ALL.forEach { cuisine ->
                        val liked = cuisine.name in state.likedCuisines
                        FilterChip(
                            selected = liked,
                            onClick = { viewModel.setCuisineLiked(cuisine.name, !liked) },
                            label = { Text(cuisine.name) },
                            leadingIcon = if (liked) {
                                { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                SectionHeader(stringResource(R.string.saved_restaurants))
            }
        }
        when {
            state.isLoading -> items(3) { ListItemPlaceholder() }
            state.saved.isEmpty() -> item {
                EmptyState(
                    icon = Icons.Rounded.FavoriteBorder,
                    title = stringResource(R.string.saved_empty_title),
                    body = stringResource(R.string.saved_empty_body),
                    actionLabel = stringResource(R.string.nav_explore),
                    onAction = onExplore,
                )
            }
            else -> items(state.saved, key = { it.id }) { restaurant ->
                RestaurantListItem(
                    restaurant = restaurant,
                    isFavorite = true,
                    onClick = { onOpenRestaurant(restaurant.id) },
                    onToggleFavorite = { viewModel.remove(restaurant) },
                )
            }
        }
    }
}

package com.darpal.foodlabrinthnew.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.createSavedStateHandle
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import com.darpal.foodlabrinthnew.ui.components.EmptyState
import com.darpal.foodlabrinthnew.ui.components.ErrorState
import com.darpal.foodlabrinthnew.ui.components.ListItemPlaceholder
import com.darpal.foodlabrinthnew.ui.components.RestaurantListItem
import com.darpal.foodlabrinthnew.ui.components.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    viewModel: ResultsViewModel = viewModel(
        factory = appViewModelFactory {
            ResultsViewModel(createSavedStateHandle(), it.restaurantRepository, it.preferencesRepository)
        },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val title = viewModel.customTitle ?: when (viewModel.mode) {
        ResultsMode.DECIDE -> stringResource(R.string.results_decide_title)
        ResultsMode.CUISINE -> viewModel.cuisine?.name.orEmpty()
        ResultsMode.SEARCH -> stringResource(R.string.nav_explore)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Column {
                        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (!state.isLoading && state.error == null) {
                            Text(
                                stringResource(R.string.results_count, state.results.size),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (viewModel.mode != ResultsMode.DECIDE) {
                item {
                    SearchField(value = query, onValueChange = viewModel::onQueryChange, onSearch = viewModel::onQueryChange)
                }
            }
            when {
                state.isLoading -> items(6) { ListItemPlaceholder() }
                state.error != null -> item { ErrorState(state.error, onRetry = viewModel::retry) }
                state.results.isEmpty() -> item {
                    EmptyState(
                        icon = Icons.Rounded.SearchOff,
                        title = stringResource(R.string.results_empty_title),
                        body = stringResource(R.string.results_empty_body),
                    )
                }
                else -> items(state.results, key = { it.id }) { restaurant ->
                    RestaurantListItem(
                        restaurant = restaurant,
                        isFavorite = restaurant.id in state.favoriteIds,
                        onClick = { onOpenRestaurant(restaurant.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(restaurant) },
                    )
                }
            }
        }
    }
}

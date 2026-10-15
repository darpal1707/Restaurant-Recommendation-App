package com.darpal.foodlabrinthnew.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.model.Area
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import com.darpal.foodlabrinthnew.ui.components.CardPlaceholder
import com.darpal.foodlabrinthnew.ui.components.CuisineImages
import com.darpal.foodlabrinthnew.ui.components.EmptyState
import com.darpal.foodlabrinthnew.ui.components.ErrorState
import com.darpal.foodlabrinthnew.ui.components.ListItemPlaceholder
import com.darpal.foodlabrinthnew.ui.components.OsmAttribution
import com.darpal.foodlabrinthnew.ui.components.RestaurantCard
import com.darpal.foodlabrinthnew.ui.components.RestaurantListItem
import com.darpal.foodlabrinthnew.ui.components.SearchField
import com.darpal.foodlabrinthnew.ui.components.SectionHeader
import com.darpal.foodlabrinthnew.ui.components.StatusBarScrim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenRestaurant: (String) -> Unit,
    onSearch: (String) -> Unit,
    onOpenCuisine: (String) -> Unit,
    onOpenCuisines: (title: String, names: Collection<String>) -> Unit,
    onSeeAll: (title: String) -> Unit,
    onDecide: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = appViewModelFactory { HomeViewModel(it.restaurantRepository, it.preferencesRepository) },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    val forYouTitle = stringResource(R.string.home_for_you)
    val moreTitle = stringResource(R.string.home_more)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        viewModel.onLocationPermissionResult(result.values.any { it })
    }
    val requestLocation = {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Column(Modifier.statusBarsPadding().padding(horizontal = 20.dp).padding(top = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.home_greeting_guest), style = MaterialTheme.typography.headlineMedium)
                            AreaPicker(state.area, onSelect = viewModel::selectArea)
                        }
                        Image(painterResource(R.drawable.logo), contentDescription = null, modifier = Modifier.size(48.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    SearchField(
                        value = query,
                        onValueChange = { query = it },
                        onSearch = { if (it.isNotBlank()) onSearch(it.trim()) },
                    )
                }
            }

            item { DecideCard(onDecide, Modifier.padding(horizontal = 20.dp)) }

            item {
                Column {
                    SectionHeader(stringResource(R.string.home_cuisines), Modifier.padding(horizontal = 20.dp))
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(Cuisine.ALL, key = { it.name }) { cuisine ->
                            CuisineBubble(cuisine, onClick = { onOpenCuisine(cuisine.name) })
                        }
                    }
                }
            }

            when {
                state.isLoading -> item { LoadingSections() }
                state.needsLocation -> item {
                    LocationPrompt(onAllow = requestLocation, onPickCity = viewModel::selectArea, modifier = Modifier.padding(horizontal = 20.dp))
                }
                state.error != null -> item { ErrorState(stringResource(state.error!!), onRetry = viewModel::refresh) }
                state.totalCount == 0 -> item {
                    EmptyState(
                        icon = Icons.Rounded.Place,
                        title = stringResource(R.string.home_nothing_title),
                        body = stringResource(R.string.home_nothing_body),
                    )
                }
                else -> {
                    item {
                        Column {
                            SectionHeader(stringResource(R.string.home_closest), Modifier.padding(horizontal = 20.dp))
                            Spacer(Modifier.height(8.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                items(state.closest, key = { it.id }) { restaurant ->
                                    RestaurantCard(
                                        restaurant = restaurant,
                                        isFavorite = restaurant.id in state.favoriteIds,
                                        onClick = { onOpenRestaurant(restaurant.id) },
                                        onToggleFavorite = { viewModel.toggleFavorite(restaurant) },
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column {
                            SectionHeader(
                                title = forYouTitle,
                                modifier = Modifier.padding(start = 20.dp, end = 8.dp),
                                action = stringResource(R.string.see_all).takeIf { state.forYou.isNotEmpty() },
                                onAction = { onOpenCuisines(forYouTitle, state.likedCuisines) },
                            )
                            if (state.forYou.isEmpty()) {
                                ForYouHint(Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                            }
                        }
                    }
                    items(state.forYou, key = { "for-you-${it.id}" }) { restaurant ->
                        RestaurantListItem(
                            restaurant = restaurant,
                            isFavorite = restaurant.id in state.favoriteIds,
                            onClick = { onOpenRestaurant(restaurant.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(restaurant) },
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }

                    if (state.more.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = moreTitle,
                                modifier = Modifier.padding(start = 20.dp, end = 8.dp),
                                action = stringResource(R.string.see_all_count, state.totalCount),
                                onAction = { onSeeAll(moreTitle) },
                            )
                        }
                        items(state.more, key = { "more-${it.id}" }) { restaurant ->
                            RestaurantListItem(
                                restaurant = restaurant,
                                isFavorite = restaurant.id in state.favoriteIds,
                                onClick = { onOpenRestaurant(restaurant.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(restaurant) },
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                    }
                }
            }

            item { OsmAttribution(Modifier.padding(horizontal = 12.dp)) }
        }
        StatusBarScrim()
    }
}

@Composable
private fun AreaPicker(area: Area, onSelect: (Area) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(area.label) },
            leadingIcon = {
                Icon(
                    if (area.isNearby) Icons.Rounded.MyLocation else Icons.Rounded.LocationCity,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            },
            trailingIcon = { Icon(Icons.Rounded.ArrowDropDown, contentDescription = stringResource(R.string.area_change)) },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            (listOf(Area.NEARBY) + Area.CITIES).forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    leadingIcon = {
                        Icon(if (option.isNearby) Icons.Rounded.MyLocation else Icons.Rounded.LocationCity, contentDescription = null)
                    },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LocationPrompt(onAllow: () -> Unit, onPickCity: (Area) -> Unit, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.secondaryContainer, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Icon(Icons.Rounded.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.location_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(stringResource(R.string.location_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onAllow) { Text(stringResource(R.string.location_allow)) }
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.location_or_city), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.height(4.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Area.CITIES.forEach { city -> SuggestionChip(onClick = { onPickCity(city) }, label = { Text(city.label) }) }
            }
        }
    }
}

@Composable
private fun DecideCard(onDecide: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = onDecide,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = colors.primary),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(colors.primary, colors.tertiary.copy(alpha = 0.85f))))
                .padding(22.dp),
        ) {
            Column(Modifier.padding(end = 72.dp)) {
                Text(stringResource(R.string.home_decide_title), style = MaterialTheme.typography.titleLarge, color = colors.onPrimary)
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.home_decide_body), style = MaterialTheme.typography.bodyMedium, color = colors.onPrimary.copy(alpha = 0.9f))
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onDecide,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.surface, contentColor = colors.primary),
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.home_decide_action))
                }
            }
            Icon(
                Icons.Rounded.Casino,
                contentDescription = null,
                tint = colors.onPrimary.copy(alpha = 0.35f),
                modifier = Modifier.align(Alignment.CenterEnd).size(72.dp),
            )
        }
    }
}

@Composable
private fun CuisineBubble(cuisine: Cuisine, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
        Surface(onClick = onClick, shape = CircleShape, modifier = Modifier.size(68.dp)) {
            Image(
                painter = painterResource(CuisineImages.forCuisine(cuisine)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(cuisine.name, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}

@Composable
private fun ForYouHint(modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.width(12.dp))
            Text(
                stringResource(R.string.home_for_you_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun LoadingSections() {
    Column(Modifier.padding(horizontal = 20.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            CardPlaceholder()
            CardPlaceholder()
        }
        Spacer(Modifier.height(24.dp))
        repeat(3) { ListItemPlaceholder() }
    }
}

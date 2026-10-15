package com.darpal.foodlabrinthnew.ui.decide

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DirectionsBike
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FreeBreakfast
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.data.model.Distance
import com.darpal.foodlabrinthnew.data.model.Meal
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import com.darpal.foodlabrinthnew.ui.components.CuisineTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecideScreen(
    onBack: () -> Unit,
    onShowResults: (route: String) -> Unit,
    viewModel: DecideViewModel = viewModel(factory = appViewModelFactory { DecideViewModel(createSavedStateHandle()) }),
) {
    val step by viewModel.step.collectAsStateWithLifecycle()
    val selectedMeal by viewModel.meal.collectAsStateWithLifecycle()
    val selectedCuisines by viewModel.cuisines.collectAsStateWithLifecycle()
    val goBack = { if (!viewModel.back()) onBack() }

    BackHandler(enabled = step > 0) { viewModel.back() }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(stringResource(R.string.decide_title))
                            Text(
                                stringResource(R.string.decide_step, step + 1, viewModel.stepCount),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = goBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    },
                )
                LinearProgressIndicator(
                    progress = { (step + 1f) / viewModel.stepCount },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                )
            }
        },
        bottomBar = {
            if (DecideStep.entries[step] == DecideStep.CUISINE) {
                Surface(tonalElevation = 3.dp) {
                    Button(
                        onClick = viewModel::next,
                        enabled = selectedCuisines.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(20.dp).height(52.dp),
                    ) {
                        Text(stringResource(R.string.decide_next) + if (selectedCuisines.isNotEmpty()) " (${selectedCuisines.size})" else "")
                    }
                }
            }
        },
    ) { padding ->
        AnimatedContent(
            targetState = step,
            modifier = Modifier.padding(padding),
            transitionSpec = {
                val forward = targetState > initialState
                slideInHorizontally { if (forward) it else -it } togetherWith slideOutHorizontally { if (forward) -it else it }
            },
            label = "decideStep",
        ) { current ->
            when (DecideStep.entries[current]) {
                DecideStep.MEAL -> OptionGrid(
                    question = stringResource(R.string.decide_meal_question),
                    options = Meal.entries.map { Option(it.label, null, mealIcon(it), it.name == selectedMeal) { viewModel.selectMeal(it) } },
                )
                DecideStep.CUISINE -> CuisineStep(selectedCuisines, viewModel::toggleCuisine)
                DecideStep.DISTANCE -> OptionGrid(
                    question = stringResource(R.string.decide_distance_question),
                    options = Distance.entries.map {
                        Option(it.label, it.description, distanceIcon(it), false) { onShowResults(viewModel.resultsRoute(it)) }
                    } + Option(stringResource(R.string.decide_any), stringResource(R.string.decide_any_body), Icons.Rounded.AutoAwesome, false) {
                        onShowResults(viewModel.resultsRoute(null))
                    },
                )
            }
        }
    }
}

private data class Option(
    val title: String,
    val subtitle: String?,
    val icon: ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit,
)

@Composable
private fun Question(text: String, hint: String? = null) {
    Column(Modifier.padding(vertical = 16.dp)) {
        Text(text, style = MaterialTheme.typography.headlineSmall)
        if (hint != null) {
            Text(hint, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun OptionGrid(question: String, options: List<Option>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { Question(question) }
        items(options, key = { it.title }) { option -> OptionCard(option) }
    }
}

@Composable
private fun OptionCard(option: Option) {
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = option.onClick,
        modifier = Modifier.aspectRatio(1f),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = if (option.selected) colors.primaryContainer else colors.surfaceContainerHigh,
        ),
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(shape = CircleShape, color = colors.primary.copy(alpha = 0.12f), modifier = Modifier.size(64.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(option.icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(option.title, style = MaterialTheme.typography.titleMedium)
            option.subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CuisineStep(selected: List<String>, onToggle: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Question(stringResource(R.string.decide_cuisine_question), stringResource(R.string.decide_cuisine_hint))
        }
        items(Cuisine.ALL, key = { it.name }) { cuisine ->
            CuisineTile(
                cuisine = cuisine,
                selected = cuisine.name in selected,
                onClick = { onToggle(cuisine.name) },
                modifier = Modifier.aspectRatio(1.2f),
            )
        }
    }
}

private fun mealIcon(meal: Meal): ImageVector = when (meal) {
    Meal.BREAKFAST -> Icons.Rounded.FreeBreakfast
    Meal.LUNCH -> Icons.Rounded.Fastfood
    Meal.DINNER -> Icons.Rounded.Restaurant
    Meal.CAFE -> Icons.Rounded.LocalCafe
    Meal.LATE_NIGHT -> Icons.Rounded.NightsStay
}

private fun distanceIcon(distance: Distance): ImageVector = when (distance) {
    Distance.WALK -> Icons.Rounded.DirectionsWalk
    Distance.SHORT_TRIP -> Icons.Rounded.DirectionsBike
    Distance.FURTHER -> Icons.Rounded.DirectionsCar
}

package com.darpal.foodlabrinthnew.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.ui.components.CuisineTile
import com.darpal.foodlabrinthnew.ui.components.SearchField

@Composable
fun ExploreScreen(onSearch: (String) -> Unit, onOpenCuisine: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(Modifier.statusBarsPadding().padding(top = 16.dp)) {
                Text(stringResource(R.string.explore_title), style = MaterialTheme.typography.headlineMedium)
                Text(
                    stringResource(R.string.explore_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                SearchField(
                    value = query,
                    onValueChange = { query = it },
                    onSearch = { if (it.isNotBlank()) onSearch(it.trim()) },
                )
            }
        }
        items(Cuisine.ALL, key = { it.name }) { cuisine ->
            CuisineTile(
                cuisine = cuisine,
                onClick = { onOpenCuisine(cuisine.name) },
                modifier = Modifier.aspectRatio(1.1f),
            )
        }
    }
}

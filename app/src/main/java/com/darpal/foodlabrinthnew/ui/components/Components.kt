package com.darpal.foodlabrinthnew.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.ui.theme.StarGold

@Composable
fun RestaurantImage(restaurant: Restaurant, modifier: Modifier = Modifier) {
    val res = remember(restaurant.id) { CuisineImages.forRestaurant(restaurant) }
    Image(
        painter = painterResource(res),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

/** "Restaurant · 350 m" */
@Composable
fun PlaceMeta(restaurant: Restaurant, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        restaurant.distanceLabel?.let { distance ->
            Icon(Icons.Rounded.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(2.dp))
            Text(distance, style = MaterialTheme.typography.labelLarge, color = color)
            Text(" · ", style = MaterialTheme.typography.labelMedium, color = color)
        }
        Text(restaurant.kind.label, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
fun StarRow(rating: Float, modifier: Modifier = Modifier, size: Int = 16) {
    Row(modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < Math.round(rating)) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                tint = StarGold,
                modifier = Modifier.size(size.dp),
            )
        }
    }
}

@Composable
fun StarRatingInput(rating: Int, onRatingChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        (1..5).forEach { value ->
            IconButton(onClick = { onRatingChange(value) }) {
                Icon(
                    imageVector = if (value <= rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "$value stars",
                    tint = StarGold,
                    modifier = Modifier.size(36.dp),
                )
            }
        }
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = modifier.size(36.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = stringResource(if (isFavorite) R.string.detail_saved else R.string.detail_save),
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** Large card for horizontal carousels. */
@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(264.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Box(Modifier.height(156.dp).fillMaxWidth()) {
            RestaurantImage(restaurant, Modifier.fillMaxSize())
            FavoriteButton(isFavorite, onToggleFavorite, Modifier.align(Alignment.TopEnd).padding(10.dp))
        }
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(restaurant.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Text(
                restaurant.cuisineLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            PlaceMeta(restaurant)
        }
    }
}

/** Compact row card for vertical lists. */
@Composable
fun RestaurantListItem(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            RestaurantImage(restaurant, Modifier.size(84.dp).clip(MaterialTheme.shapes.medium))
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(restaurant.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    restaurant.cuisineLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                PlaceMeta(restaurant)
                restaurant.address.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = stringResource(if (isFavorite) R.string.detail_saved else R.string.detail_save),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Image tile with a dark gradient and the cuisine name. */
@Composable
fun CuisineTile(
    cuisine: Cuisine,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        border = if (selected) androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(CuisineImages.forCuisine(cuisine)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(0.35f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.75f)),
                ),
            )
            if (selected) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(28.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
            Text(
                cuisine.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart).padding(14.dp),
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) { Text(action) }
        }
    }
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_hint), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Rounded.Clear, contentDescription = stringResource(R.string.clear))
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedBorderColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(value) }),
    )
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(88.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

@Composable
fun ErrorState(message: String?, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    EmptyState(
        icon = Icons.Rounded.WifiOff,
        title = stringResource(R.string.generic_error),
        body = message.orEmpty(),
        actionLabel = stringResource(R.string.retry),
        onAction = onRetry,
        modifier = modifier,
    )
}

/** Animated placeholder used while data loads. */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing)),
        label = "shimmerX",
    )
    val base = MaterialTheme.colorScheme.surfaceContainerHighest
    val highlight = MaterialTheme.colorScheme.surfaceContainerLow
    background(Brush.linearGradient(listOf(base, highlight, base), start = Offset(x, 0f), end = Offset(x + 400f, 400f)))
}

@Composable
fun ListItemPlaceholder(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(84.dp).clip(MaterialTheme.shapes.medium).shimmer())
        Column(Modifier.padding(start = 14.dp).weight(1f)) {
            Box(Modifier.fillMaxWidth(0.7f).height(16.dp).clip(MaterialTheme.shapes.extraSmall).shimmer())
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth(0.5f).height(12.dp).clip(MaterialTheme.shapes.extraSmall).shimmer())
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth(0.3f).height(12.dp).clip(MaterialTheme.shapes.extraSmall).shimmer())
        }
    }
}

@Composable
fun CardPlaceholder(modifier: Modifier = Modifier) {
    Column(modifier.width(264.dp)) {
        Box(Modifier.fillMaxWidth().height(156.dp).clip(MaterialTheme.shapes.large).shimmer())
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth(0.7f).height(16.dp).clip(MaterialTheme.shapes.extraSmall).shimmer())
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(0.4f).height(12.dp).clip(MaterialTheme.shapes.extraSmall).shimmer())
    }
}

@Composable
fun Avatar(name: String, modifier: Modifier = Modifier, size: Int = 40) {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer, modifier = modifier.size(size.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                name.firstOrNull()?.uppercase() ?: "?",
                style = if (size >= 64) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

/** Attribution required by the OpenStreetMap licence (ODbL). */
@Composable
fun OsmAttribution(modifier: Modifier = Modifier) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    TextButton(onClick = { uriHandler.openUri("https://www.openstreetmap.org/copyright") }, modifier = modifier) {
        Text(
            stringResource(R.string.osm_attribution),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Solid strip behind the transparent status bar, so content scrolling under it doesn't collide with
 * the clock and icons. Place it last in a Box that fills the screen.
 */
@Composable
fun StatusBarScrim(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.94f)),
    )
}

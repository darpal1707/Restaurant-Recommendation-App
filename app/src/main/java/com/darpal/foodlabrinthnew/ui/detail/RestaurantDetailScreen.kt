package com.darpal.foodlabrinthnew.ui.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.darpal.foodlabrinthnew.FeatureFlags
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.model.Photo
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.data.model.Review
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import com.darpal.foodlabrinthnew.ui.components.Avatar
import com.darpal.foodlabrinthnew.ui.components.ErrorState
import com.darpal.foodlabrinthnew.ui.components.OsmAttribution
import com.darpal.foodlabrinthnew.ui.components.PlaceMeta
import com.darpal.foodlabrinthnew.ui.components.RestaurantImage
import com.darpal.foodlabrinthnew.ui.components.StarRatingInput
import com.darpal.foodlabrinthnew.ui.components.StarRow
import com.darpal.foodlabrinthnew.ui.components.rememberOsmMapView
import com.darpal.foodlabrinthnew.ui.components.toOsm
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    viewModel: RestaurantDetailViewModel = viewModel(
        factory = appViewModelFactory {
            RestaurantDetailViewModel(
                savedState = createSavedStateHandle(),
                restaurantRepository = it.restaurantRepository,
                preferences = it.preferencesRepository,
                reviewRepository = if (FeatureFlags.ACCOUNTS_ENABLED) it.reviewRepository else null,
                authRepository = if (FeatureFlags.ACCOUNTS_ENABLED) it.authRepository else null,
            )
        },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showReviewSheet by rememberSaveable { mutableStateOf(false) }
    val signInLabel = stringResource(R.string.sign_in)

    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is DetailEvent.Message -> scope.launch { snackbar.showSnackbar(context.getString(event.text)) }
                is DetailEvent.SignInRequired -> scope.launch {
                    val result = snackbar.showSnackbar(context.getString(event.text), actionLabel = signInLabel)
                    if (result == SnackbarResult.ActionPerformed) onSignIn()
                }
                DetailEvent.ReviewPosted -> showReviewSheet = false
            }
        }
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) viewModel.uploadPhoto { bitmap.toJpeg() }
    }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.uploadPhoto { context.contentResolver.readScaledJpeg(uri) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        val restaurant = state.restaurant
        when {
            restaurant != null -> DetailContent(
                restaurant = restaurant,
                state = state,
                bottomPadding = padding.calculateBottomPadding(),
                onBack = onBack,
                onToggleFavorite = viewModel::toggleFavorite,
                onShare = { share(context, restaurant) },
                onCall = { restaurant.phone?.let { phone -> openIntent(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone)))) } },
                onWebsite = { restaurant.website?.let { site -> openIntent(context, Intent(Intent.ACTION_VIEW, Uri.parse(site))) } },
                onOpenOsm = { restaurant.osmUrl?.let { url -> openIntent(context, Intent(Intent.ACTION_VIEW, Uri.parse(url))) } },
                onDirections = {
                    if (!openDirections(context, restaurant)) {
                        scope.launch { snackbar.showSnackbar(context.getString(R.string.detail_no_maps_app)) }
                    }
                },
                onWriteReview = { if (viewModel.requireUser(R.string.detail_login_to_review)) showReviewSheet = true },
                onTakePhoto = {
                    if (viewModel.requireUser(R.string.detail_login_to_upload)) {
                        try {
                            takePicture.launch(null)
                        } catch (e: ActivityNotFoundException) {
                            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                },
                onPickPhoto = {
                    if (viewModel.requireUser(R.string.detail_login_to_upload)) {
                        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                },
            )
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else -> Column(Modifier.fillMaxSize().statusBarsPadding()) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                }
                ErrorState(stringResource(R.string.detail_not_found), onRetry = viewModel::retry)
            }
        }
    }

    val restaurant = state.restaurant
    if (showReviewSheet && restaurant != null) {
        ReviewSheet(
            restaurantName = restaurant.name,
            isPosting = state.isPostingReview,
            onDismiss = { showReviewSheet = false },
            onSubmit = viewModel::submitReview,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    restaurant: Restaurant,
    state: DetailUiState,
    bottomPadding: androidx.compose.ui.unit.Dp,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onCall: () -> Unit,
    onWebsite: () -> Unit,
    onOpenOsm: () -> Unit,
    onDirections: () -> Unit,
    onWriteReview: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit,
) {
    val listState = rememberLazyListState()
    val collapsed by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState, contentPadding = PaddingValues(bottom = bottomPadding + 32.dp)) {
            item { Hero(restaurant, state.photos.firstOrNull(), state.isFavorite, onBack, onShare, onToggleFavorite) }

            item {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(restaurant.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    PlaceMeta(restaurant)
                    if (restaurant.cityState.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(restaurant.cityState, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    val cuisines = restaurant.categories.filterNot { it == restaurant.kind.label }
                    if (cuisines.isNotEmpty() || restaurant.features.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            cuisines.forEach { category ->
                                SuggestionChip(onClick = {}, label = { Text(category) })
                            }
                            restaurant.features.forEach { feature ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(feature) },
                                    icon = { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        ActionButton(Icons.Rounded.Map, stringResource(R.string.detail_directions), onDirections)
                        if (restaurant.phone != null) ActionButton(Icons.Rounded.Call, stringResource(R.string.detail_call), onCall)
                        if (restaurant.website != null) ActionButton(Icons.Rounded.Language, stringResource(R.string.detail_website), onWebsite)
                        ActionButton(Icons.Rounded.Share, stringResource(R.string.detail_share), onShare)
                        if (state.socialEnabled) ActionButton(Icons.Rounded.Edit, stringResource(R.string.detail_review), onWriteReview)
                        ActionButton(
                            if (state.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            stringResource(if (state.isFavorite) R.string.detail_saved else R.string.detail_save),
                            onToggleFavorite,
                        )
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp, vertical = 20.dp))
                }
            }

            item { LocationSection(restaurant, onDirections) }
            item { HoursSection(restaurant) }
            if (state.socialEnabled) {
                item { PhotosSection(state.photos, state.isUploading, onTakePhoto, onPickPhoto) }

                item {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.detail_reviews), style = MaterialTheme.typography.titleLarge)
                            state.averageUserRating?.let {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    StarRow(it)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        stringResource(R.string.detail_reviews_count, state.reviews.size),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        FilledTonalButton(onClick = onWriteReview) {
                            Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.detail_write_review))
                        }
                    }
                }
                if (state.reviews.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.detail_reviews_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                } else {
                    items(state.reviews, key = { it.id }) { review -> ReviewItem(review) }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    if (restaurant.osmUrl != null) {
                        TextButton(onClick = onOpenOsm) {
                            Icon(Icons.Rounded.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.detail_view_osm))
                        }
                    }
                    OsmAttribution()
                }
            }
        }

        // Compact app bar that fades in once the hero image has scrolled away.
        AnimatedVisibility(visible = collapsed, enter = fadeIn(), exit = fadeOut()) {
            TopAppBar(
                title = { Text(restaurant.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            if (state.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(R.string.detail_save),
                            tint = if (state.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            )
        }
    }
}

@Composable
private fun Hero(
    restaurant: Restaurant,
    photo: Photo?,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(300.dp)) {
        if (photo != null) {
            AsyncImage(
                model = photo.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            RestaurantImage(restaurant, Modifier.fillMaxSize())
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.45f),
                    0.3f to Color.Transparent,
                    0.7f to Color.Transparent,
                    1f to MaterialTheme.colorScheme.surface,
                ),
            ),
        )
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.back), onBack)
            Spacer(Modifier.weight(1f))
            CircleIconButton(Icons.Rounded.Share, stringResource(R.string.detail_share), onShare)
            Spacer(Modifier.width(8.dp))
            CircleIconButton(
                if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                stringResource(R.string.detail_save),
                onToggleFavorite,
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun CircleIconButton(icon: ImageVector, description: String, onClick: () -> Unit, tint: Color = MaterialTheme.colorScheme.onSurface) {
    Surface(onClick = onClick, shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), modifier = Modifier.size(44.dp)) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = description, tint = tint) }
    }
}

@Composable
private fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(56.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(Modifier.padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun LocationSection(restaurant: Restaurant, onDirections: () -> Unit) {
    Column {
        SectionTitle(Icons.Rounded.Place, stringResource(R.string.detail_location))
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            PreviewMap(restaurant, onClick = onDirections)
            Text(
                restaurant.fullAddress.ifBlank { restaurant.cityState }.ifBlank { "%.5f, %.5f".format(restaurant.latitude, restaurant.longitude) },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HoursSection(restaurant: Restaurant) {
    Column {
        SectionTitle(Icons.Rounded.Schedule, stringResource(R.string.detail_hours))
        Column(Modifier.padding(horizontal = 20.dp)) {
            if (restaurant.hours.isEmpty()) {
                Text(stringResource(R.string.detail_hours_unknown), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                restaurant.hours.forEach { hours ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(hours.day, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(hours.time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PhotosSection(photos: List<Photo>, isUploading: Boolean, onTakePhoto: () -> Unit, onPickPhoto: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { SectionTitle(Icons.Rounded.PhotoCamera, stringResource(R.string.detail_photos)) }
            if (isUploading) {
                CircularProgressIndicator(Modifier.padding(end = 20.dp).size(20.dp), strokeWidth = 2.dp)
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { AddPhotoTile(Icons.Rounded.PhotoCamera, stringResource(R.string.detail_add_photo), onTakePhoto) }
            item { AddPhotoTile(Icons.Rounded.PhotoLibrary, stringResource(R.string.detail_pick_photo), onPickPhoto) }
            items(photos, key = { it.id }) { photo ->
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(132.dp).clip(MaterialTheme.shapes.medium),
                )
            }
        }
        if (photos.isEmpty()) {
            Text(
                stringResource(R.string.detail_photos_empty),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AddPhotoTile(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(132.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    val name = review.userName ?: "Food lover"
    Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(name)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleSmall)
                Text(review.date.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StarRow(review.stars, size = 14)
        }
        if (review.text.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(review.text, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewSheet(
    restaurantName: String,
    isPosting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (stars: Int, text: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var rating by rememberSaveable { mutableIntStateOf(0) }
    var text by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<Int?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp).navigationBarsPadding().padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Rounded.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.review_title, restaurantName), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            StarRatingInput(rating, onRatingChange = { rating = it; error = null })
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it; error = null },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.review_hint)) },
                minLines = 4,
                shape = MaterialTheme.shapes.medium,
                isError = error != null,
                supportingText = error?.let { { Text(stringResource(it)) } },
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    error = when {
                        rating == 0 -> R.string.review_rating_required
                        text.isBlank() -> R.string.review_text_required
                        else -> null
                    }
                    if (error == null) onSubmit(rating, text)
                },
                enabled = !isPosting,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (isPosting) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text(stringResource(R.string.review_submit))
            }
        }
    }
}

/** Non-interactive OpenStreetMap preview with one pin; tapping it opens directions. */
@Composable
private fun PreviewMap(restaurant: Restaurant, onClick: () -> Unit) {
    val context = LocalContext.current
    val mapView = rememberOsmMapView(interactive = false)
    DisposableEffect(mapView, restaurant.id) {
        val marker = Marker(mapView).apply {
            position = restaurant.location.toOsm()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            setIcon(ContextCompat.getDrawable(context, R.drawable.ic_map_pin))
        }
        mapView.overlays.add(marker)
        mapView.controller.setZoom(17.0)
        mapView.controller.setCenter(restaurant.location.toOsm())
        mapView.invalidate()
        onDispose { mapView.overlays.remove(marker) }
    }
    Box(Modifier.fillMaxWidth().height(180.dp)) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
        // Transparent layer: the map itself ignores touches, so the whole preview acts as a button.
        Box(Modifier.fillMaxSize().clickable(onClick = onClick))
    }
}

private fun mapsUrl(restaurant: Restaurant): String {
    val target = "${restaurant.latitude},${restaurant.longitude}"
    return "https://www.google.com/maps/search/?api=1&query=$target"
}

private fun openDirections(context: Context, restaurant: Restaurant): Boolean {
    val destination = "${restaurant.latitude},${restaurant.longitude}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destination"))
    return try {
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}

private fun openIntent(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Nothing can handle it (e.g. no dialer on a tablet); ignore.
    }
}

private fun share(context: Context, restaurant: Restaurant) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, restaurant.name)
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.detail_share_text, restaurant.name, mapsUrl(restaurant)))
    }
    context.startActivity(Intent.createChooser(send, context.getString(R.string.detail_share_chooser)))
}

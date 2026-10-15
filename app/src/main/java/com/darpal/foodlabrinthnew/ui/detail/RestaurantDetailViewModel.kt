package com.darpal.foodlabrinthnew.ui.detail

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.AuthRepository
import com.darpal.foodlabrinthnew.data.AuthUser
import com.darpal.foodlabrinthnew.data.PlacesState
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.RestaurantRepository
import com.darpal.foodlabrinthnew.data.ReviewRepository
import com.darpal.foodlabrinthnew.data.model.Photo
import com.darpal.foodlabrinthnew.data.model.Restaurant
import com.darpal.foodlabrinthnew.data.model.Review
import com.darpal.foodlabrinthnew.ui.navigation.Routes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val restaurant: Restaurant? = null,
    val isFavorite: Boolean = false,
    /** False while accounts are disabled: reviews and photos are hidden. */
    val socialEnabled: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val photos: List<Photo> = emptyList(),
    val user: AuthUser? = null,
    val isUploading: Boolean = false,
    val isPostingReview: Boolean = false,
) {
    val averageUserRating: Float?
        get() = reviews.filter { it.stars > 0f }.takeIf { it.isNotEmpty() }?.map { it.stars }?.average()?.toFloat()
}

sealed interface DetailEvent {
    data class Message(@StringRes val text: Int) : DetailEvent
    data class SignInRequired(@StringRes val text: Int) : DetailEvent
    data object ReviewPosted : DetailEvent
}

/**
 * [reviewRepository] and [authRepository] are null while accounts are disabled
 * (FeatureFlags.ACCOUNTS_ENABLED), so Firebase is never initialised.
 */
class RestaurantDetailViewModel(
    savedState: SavedStateHandle,
    private val restaurantRepository: RestaurantRepository,
    private val preferences: PreferencesRepository,
    private val reviewRepository: ReviewRepository?,
    private val authRepository: AuthRepository?,
) : ViewModel() {

    private val id: String = checkNotNull(savedState[Routes.ARG_ID])

    private data class Busy(val uploading: Boolean = false, val posting: Boolean = false)
    private val busy = MutableStateFlow(Busy())

    private val events = Channel<DetailEvent>(Channel.BUFFERED)
    val eventFlow: Flow<DetailEvent> = events.receiveAsFlow()

    private val reviews: Flow<List<Review>> =
        reviewRepository?.reviews(id)?.catch { emit(emptyList()) }?.onStart { emit(emptyList()) } ?: flowOf(emptyList())
    private val photos: Flow<List<Photo>> =
        reviewRepository?.photos(id)?.catch { emit(emptyList()) }?.onStart { emit(emptyList()) } ?: flowOf(emptyList())
    private val social = combine(reviews, photos, authRepository?.user ?: flowOf(null), busy, ::Social)

    private data class Social(val reviews: List<Review>, val photos: List<Photo>, val user: AuthUser?, val busy: Busy)

    val uiState: StateFlow<DetailUiState> = combine(
        restaurantRepository.state,
        preferences.savedRestaurants,
        social,
    ) { state, saved, social ->
        val fromArea = (state as? PlacesState.Ready)?.restaurants?.firstOrNull { it.id == id }
        val fromSaved = saved.firstOrNull { it.id == id }
        DetailUiState(
            isLoading = state is PlacesState.Loading && fromSaved == null,
            // Saved snapshots let a place open even after switching to another area.
            restaurant = fromArea ?: fromSaved,
            isFavorite = fromSaved != null,
            socialEnabled = reviewRepository != null,
            reviews = social.reviews,
            photos = social.photos,
            user = social.user,
            isUploading = social.busy.uploading,
            isPostingReview = social.busy.posting,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState())

    fun toggleFavorite() {
        val restaurant = uiState.value.restaurant ?: return
        viewModelScope.launch {
            val saved = preferences.toggleFavorite(restaurant)
            events.send(DetailEvent.Message(if (saved) R.string.detail_saved_message else R.string.detail_removed_message))
        }
    }

    /** Returns false (and asks the user to sign in) when nobody is signed in. */
    fun requireUser(@StringRes reason: Int): Boolean {
        if (authRepository?.user?.value != null) return true
        events.trySend(DetailEvent.SignInRequired(reason))
        return false
    }

    fun submitReview(stars: Int, text: String) {
        val repository = reviewRepository ?: return
        val user = authRepository?.user?.value ?: return
        viewModelScope.launch {
            busy.value = busy.value.copy(posting = true)
            try {
                repository.addReview(id, stars.toFloat(), text.trim(), user)
                events.send(DetailEvent.ReviewPosted)
                events.send(DetailEvent.Message(R.string.detail_review_posted))
            } catch (e: Exception) {
                events.send(DetailEvent.Message(R.string.generic_error))
            } finally {
                busy.value = busy.value.copy(posting = false)
            }
        }
    }

    /** [readJpeg] runs off the main thread and returns compressed image bytes, or null on failure. */
    fun uploadPhoto(readJpeg: suspend () -> ByteArray?) {
        val repository = reviewRepository ?: return
        val user = authRepository?.user?.value ?: return
        viewModelScope.launch {
            busy.value = busy.value.copy(uploading = true)
            events.send(DetailEvent.Message(R.string.detail_uploading))
            try {
                val bytes = readJpeg() ?: error("Could not read image")
                repository.uploadPhoto(id, bytes, user.uid)
                events.send(DetailEvent.Message(R.string.detail_upload_done))
            } catch (e: Exception) {
                events.send(DetailEvent.Message(R.string.generic_error))
            } finally {
                busy.value = busy.value.copy(uploading = false)
            }
        }
    }

    fun retry() = restaurantRepository.refresh()
}

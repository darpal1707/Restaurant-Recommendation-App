package com.darpal.foodlabrinthnew.ui.detail

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.AuthRepository
import com.darpal.foodlabrinthnew.data.AuthUser
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.data.Resource
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val restaurant: Restaurant? = null,
    val reviews: List<Review> = emptyList(),
    val photos: List<Photo> = emptyList(),
    val isFavorite: Boolean = false,
    val user: AuthUser? = null,
    val isUploading: Boolean = false,
    val isPostingReview: Boolean = false,
) {
    val averageUserRating: Float? get() = reviews.filter { it.stars > 0f }.takeIf { it.isNotEmpty() }?.map { it.stars }?.average()?.toFloat()
}

sealed interface DetailEvent {
    data class Message(@StringRes val text: Int) : DetailEvent
    data class SignInRequired(@StringRes val text: Int) : DetailEvent
    data object ReviewPosted : DetailEvent
}

class RestaurantDetailViewModel(
    savedState: SavedStateHandle,
    private val restaurantRepository: RestaurantRepository,
    private val reviewRepository: ReviewRepository,
    private val preferences: PreferencesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val id: String = checkNotNull(savedState[Routes.ARG_ID])

    private val busy = MutableStateFlow(Busy())
    private data class Busy(val uploading: Boolean = false, val posting: Boolean = false)

    private val events = Channel<DetailEvent>(Channel.BUFFERED)
    val eventFlow: Flow<DetailEvent> = events.receiveAsFlow()

    val uiState: StateFlow<DetailUiState> = combine(
        restaurantRepository.restaurants,
        reviewRepository.reviews(id).catch { emit(emptyList()) }.onStart { emit(emptyList()) },
        reviewRepository.photos(id).catch { emit(emptyList()) }.onStart { emit(emptyList()) },
        combine(preferences.favoriteIds, authRepository.user, ::Pair),
        busy,
    ) { resource, reviews, photos, (favorites, user), work ->
        DetailUiState(
            isLoading = resource is Resource.Loading,
            restaurant = (resource as? Resource.Success)?.data?.firstOrNull { it.id == id },
            reviews = reviews,
            photos = photos,
            isFavorite = id in favorites,
            user = user,
            isUploading = work.uploading,
            isPostingReview = work.posting,
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
        if (authRepository.user.value != null) return true
        events.trySend(DetailEvent.SignInRequired(reason))
        return false
    }

    fun submitReview(stars: Int, text: String) {
        val user = authRepository.user.value ?: return
        viewModelScope.launch {
            busy.value = busy.value.copy(posting = true)
            try {
                reviewRepository.addReview(id, stars.toFloat(), text.trim(), user)
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
        val user = authRepository.user.value ?: return
        viewModelScope.launch {
            busy.value = busy.value.copy(uploading = true)
            events.send(DetailEvent.Message(R.string.detail_uploading))
            try {
                val bytes = readJpeg() ?: error("Could not read image")
                reviewRepository.uploadPhoto(id, bytes, user.uid)
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

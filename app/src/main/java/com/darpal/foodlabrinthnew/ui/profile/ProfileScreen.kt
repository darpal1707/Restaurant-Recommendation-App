package com.darpal.foodlabrinthnew.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.AuthRepository
import com.darpal.foodlabrinthnew.data.AuthUser
import com.darpal.foodlabrinthnew.data.PreferencesRepository
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import com.darpal.foodlabrinthnew.ui.auth.AuthScreen
import com.darpal.foodlabrinthnew.ui.auth.clearGoogleCredentials
import com.darpal.foodlabrinthnew.ui.components.Avatar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(val user: AuthUser? = null, val savedCount: Int = 0, val likedCount: Int = 0)

class ProfileViewModel(private val auth: AuthRepository, preferences: PreferencesRepository) : ViewModel() {
    val uiState: StateFlow<ProfileUiState> = combine(auth.user, preferences.favoriteIds, preferences.likedCuisines) { user, saved, liked ->
        ProfileUiState(user, saved.size, liked.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState(auth.user.value))

    fun signOut() = auth.signOut()
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = appViewModelFactory { ProfileViewModel(it.authRepository, it.preferencesRepository) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val user = state.user
    if (user == null) {
        AuthScreen()
    } else {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        SignedInProfile(
            user = user,
            state = state,
            onSignOut = {
                viewModel.signOut()
                scope.launch { clearGoogleCredentials(context) }
            },
        )
    }
}

@Composable
private fun SignedInProfile(user: AuthUser, state: ProfileUiState, onSignOut: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))
        if (user.photoUrl != null) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(96.dp).clip(CircleShape),
            )
        } else {
            Avatar(user.label, size = 96)
        }
        Spacer(Modifier.height(12.dp))
        Text(user.label, style = MaterialTheme.typography.titleLarge)
        user.email?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Icons.Rounded.Favorite, state.savedCount, stringResource(R.string.profile_saved_count), Modifier.weight(1f))
            StatCard(Icons.Rounded.Restaurant, state.likedCount, stringResource(R.string.profile_liked_count), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(stringResource(R.string.profile_about), style = MaterialTheme.typography.titleSmall)
                    Text(stringResource(R.string.profile_about_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Icon(Icons.Rounded.PowerSettingsNew, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.profile_sign_out))
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, value: Int, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(8.dp))
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

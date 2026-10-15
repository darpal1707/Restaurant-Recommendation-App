package com.darpal.foodlabrinthnew.ui.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { SIGN_IN, SIGN_UP, RESET }

data class AuthUiState(
    val mode: AuthMode = AuthMode.SIGN_IN,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val error: Int? = null,
    @StringRes val info: Int? = null,
    val isLoading: Boolean = false,
)

class AuthViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun setMode(mode: AuthMode) = _state.update {
        AuthUiState(mode = mode, email = it.email)
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null, error = null) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = null, error = null, info = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, passwordError = null, error = null) }

    fun submit() {
        val s = _state.value
        val email = s.email.trim()
        val nameError = if (s.mode == AuthMode.SIGN_UP && s.name.isBlank()) R.string.auth_error_name else null
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) R.string.auth_error_email else null
        val passwordError = if (s.mode != AuthMode.RESET && s.password.length < 6) R.string.auth_error_password else null
        if (nameError != null || emailError != null || passwordError != null) {
            _state.update { it.copy(nameError = nameError, emailError = emailError, passwordError = passwordError) }
            return
        }
        launchAuth {
            when (s.mode) {
                AuthMode.SIGN_IN -> auth.signIn(email, s.password)
                AuthMode.SIGN_UP -> auth.signUp(s.name.trim(), email, s.password)
                AuthMode.RESET -> {
                    auth.sendPasswordReset(email)
                    _state.update { it.copy(info = R.string.auth_reset_sent) }
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) = launchAuth { auth.signInWithGoogle(idToken) }

    fun onGoogleFailed() = _state.update { it.copy(error = R.string.auth_error_google, isLoading = false) }

    fun setLoading(loading: Boolean) = _state.update { it.copy(isLoading = loading) }

    private fun launchAuth(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, info = null) }
            try {
                block()
                _state.update { it.copy(isLoading = false, password = "") }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.toMessage()) }
            }
        }
    }

    @StringRes
    private fun Exception.toMessage(): Int = when (this) {
        is FirebaseAuthWeakPasswordException -> R.string.auth_error_password
        is FirebaseAuthInvalidUserException -> R.string.auth_error_no_user
        is FirebaseAuthUserCollisionException -> R.string.auth_error_exists
        is FirebaseAuthInvalidCredentialsException -> R.string.auth_error_credentials
        is FirebaseNetworkException -> R.string.auth_error_network
        else -> R.string.generic_error
    }
}

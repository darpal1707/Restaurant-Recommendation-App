package com.darpal.foodlabrinthnew.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.ui.appViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(factory = appViewModelFactory { AuthViewModel(it.authRepository) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val onGoogle: () -> Unit = {
        scope.launch {
            viewModel.setLoading(true)
            try {
                viewModel.signInWithGoogle(requestGoogleIdToken(context))
            } catch (e: GetCredentialCancellationException) {
                viewModel.setLoading(false)
            } catch (e: Exception) {
                viewModel.onGoogleFailed()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(R.drawable.logo), contentDescription = null, modifier = Modifier.size(88.dp))
        Spacer(Modifier.height(20.dp))

        AnimatedContent(
            targetState = state.mode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "authMode",
        ) { mode ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val (title, body) = when (mode) {
                    AuthMode.SIGN_IN -> R.string.auth_welcome to R.string.auth_welcome_body
                    AuthMode.SIGN_UP -> R.string.auth_signup_title to R.string.auth_signup_body
                    AuthMode.RESET -> R.string.auth_reset_title to R.string.auth_reset_body
                }
                Text(stringResource(title), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))

                if (mode == AuthMode.SIGN_UP) {
                    AuthField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = stringResource(R.string.auth_full_name),
                        icon = Icons.Rounded.Person,
                        error = state.nameError,
                        keyboardType = KeyboardType.Text,
                    )
                    Spacer(Modifier.height(12.dp))
                }
                AuthField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = stringResource(R.string.auth_email),
                    icon = Icons.Rounded.Email,
                    error = state.emailError,
                    keyboardType = KeyboardType.Email,
                    imeAction = if (mode == AuthMode.RESET) ImeAction.Done else ImeAction.Next,
                    onDone = viewModel::submit,
                )
                if (mode != AuthMode.RESET) {
                    Spacer(Modifier.height(12.dp))
                    PasswordField(state.password, viewModel::onPasswordChange, state.passwordError, onDone = viewModel::submit)
                }
                if (mode == AuthMode.SIGN_IN) {
                    TextButton(onClick = { viewModel.setMode(AuthMode.RESET) }, modifier = Modifier.align(Alignment.End)) {
                        Text(stringResource(R.string.auth_forgot))
                    }
                } else {
                    Spacer(Modifier.height(16.dp))
                }

                (state.error ?: state.info)?.let { message ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (state.error != null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    ) {
                        Text(
                            stringResource(message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (state.error != null) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }

                Button(
                    onClick = viewModel::submit,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            stringResource(
                                when (mode) {
                                    AuthMode.SIGN_IN -> R.string.sign_in
                                    AuthMode.SIGN_UP -> R.string.auth_sign_up
                                    AuthMode.RESET -> R.string.auth_reset_send
                                },
                            ),
                        )
                    }
                }

                if (mode != AuthMode.RESET) {
                    Row(Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(Modifier.weight(1f))
                        Text(
                            stringResource(R.string.auth_or),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                        HorizontalDivider(Modifier.weight(1f))
                    }
                    OutlinedButton(
                        onClick = onGoogle,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) {
                        Icon(Icons.Rounded.AccountCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.auth_google))
                    }
                }

                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    when (mode) {
                        AuthMode.SIGN_IN -> {
                            Text(stringResource(R.string.auth_no_account), style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { viewModel.setMode(AuthMode.SIGN_UP) }) { Text(stringResource(R.string.auth_create_account)) }
                        }
                        AuthMode.SIGN_UP -> {
                            Text(stringResource(R.string.auth_have_account), style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { viewModel.setMode(AuthMode.SIGN_IN) }) { Text(stringResource(R.string.sign_in)) }
                        }
                        AuthMode.RESET -> TextButton(onClick = { viewModel.setMode(AuthMode.SIGN_IN) }) {
                            Text(stringResource(R.string.auth_back_to_login))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    error: Int?,
    keyboardType: KeyboardType,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        isError = error != null,
        supportingText = error?.let { { Text(stringResource(it)) } },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
    )
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit, error: Int?, onDone: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.auth_password)) },
        leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                    contentDescription = stringResource(if (visible) R.string.auth_hide_password else R.string.auth_show_password),
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = error != null,
        supportingText = error?.let { { Text(stringResource(it)) } },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
    )
}

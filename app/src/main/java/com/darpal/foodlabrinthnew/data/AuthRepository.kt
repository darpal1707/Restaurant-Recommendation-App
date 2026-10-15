package com.darpal.foodlabrinthnew.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
) {
    /** Name shown in the UI: display name, else the part of the email before "@". */
    val label: String
        get() = displayName?.takeIf { it.isNotBlank() }
            ?: email?.substringBefore('@')?.takeIf { it.isNotBlank() }
            ?: "Foodie"
}

class AuthRepository(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
) {
    private val _user = MutableStateFlow(auth.currentUser?.toAuthUser())
    val user: StateFlow<AuthUser?> = _user.asStateFlow()

    init {
        auth.addAuthStateListener { _user.value = it.currentUser?.toAuthUser() }
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(fullName: String, email: String, password: String) {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user ?: return
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build()).await()
        // The auth listener has already fired with the old (empty) name; publish the update.
        _user.value = user.toAuthUser().copy(displayName = fullName)
        // Profile record for the database. Unlike the original app, the password is never stored.
        runCatching {
            database.reference.child("user").child(user.uid)
                .setValue(mapOf("userid" to user.uid, "email" to email, "fullname" to fullName))
                .await()
        }
    }

    suspend fun signInWithGoogle(idToken: String) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() = auth.signOut()

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl?.toString(),
    )
}

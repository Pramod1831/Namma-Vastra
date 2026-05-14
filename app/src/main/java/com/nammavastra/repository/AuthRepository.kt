package com.nammavastra.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

data class CachedSession(
    val email: String = "",
    val displayName: String = ""
)

class AuthRepository(
    context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val prefs = context.applicationContext.getSharedPreferences("auth_session", Context.MODE_PRIVATE)

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun cachedSession(): CachedSession = CachedSession(
        email = prefs.getString("email", "").orEmpty(),
        displayName = prefs.getString("display_name", "").orEmpty()
    )

    fun addAuthStateListener(listener: AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await().user
            ?.also(::persistSession)
            ?: error("Unable to sign in.")
    }

    suspend fun signUp(email: String, password: String, name: String): Result<FirebaseUser> = runCatching {
        val user = auth.createUserWithEmailAndPassword(email.trim(), password).await().user
            ?: error("Unable to create account.")
        if (name.isNotBlank()) {
            user.updateProfile(
                userProfileChangeRequest {
                    displayName = name.trim()
                }
            ).await()
        }
        user.reload().await()
        auth.currentUser
            ?.also(::persistSession)
            ?: user.also(::persistSession)
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await().user
            ?.also(::persistSession)
            ?: error("Unable to sign in with Google.")
    }

    fun signOut() {
        clearCachedSession()
        auth.signOut()
    }

    fun persistSession(user: FirebaseUser?) {
        if (user == null) return
        prefs.edit()
            .putString("email", user.email.orEmpty())
            .putString("display_name", user.displayName.orEmpty())
            .apply()
    }

    fun clearCachedSession() {
        prefs.edit().clear().apply()
    }
}

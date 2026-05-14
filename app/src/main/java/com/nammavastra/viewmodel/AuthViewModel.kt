package com.nammavastra.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nammavastra.BuildConfig
import com.nammavastra.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSignUp: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: FirebaseUser? = null,
    val cachedEmail: String = "",
    val cachedDisplayName: String = "",
    val isAdmin: Boolean = false
) {
    val resolvedEmail: String
        get() = currentUser?.email ?: cachedEmail

    val resolvedDisplayName: String
        get() = currentUser?.displayName ?: cachedDisplayName

    val isAuthenticated: Boolean
        get() = resolvedEmail.isNotBlank()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val adminEmail = BuildConfig.ADMIN_EMAIL

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val cached = repository.cachedSession()
        repository.persistSession(auth.currentUser)
        update {
            copy(
                currentUser = auth.currentUser,
                cachedEmail = auth.currentUser?.email ?: cached.email,
                cachedDisplayName = auth.currentUser?.displayName ?: cached.displayName,
                isAdmin = (auth.currentUser?.email ?: cached.email).equals(adminEmail, ignoreCase = true),
                isLoading = false
            )
        }
    }

    private val initialCached = repository.cachedSession()
    private val _uiState = MutableStateFlow(
        AuthUiState(
            currentUser = repository.currentUser(),
            cachedEmail = initialCached.email,
            cachedDisplayName = initialCached.displayName,
            isAdmin = (repository.currentUser()?.email ?: initialCached.email).equals(adminEmail, ignoreCase = true)
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        repository.addAuthStateListener(authStateListener)
    }

    fun updateEmail(value: String) = update { copy(email = value, errorMessage = null) }
    fun updateName(value: String) = update { copy(name = value, errorMessage = null) }
    fun updatePassword(value: String) = update { copy(password = value, errorMessage = null) }
    fun updateConfirmPassword(value: String) = update { copy(confirmPassword = value, errorMessage = null) }
    fun toggleMode() = update { copy(isSignUp = !isSignUp, errorMessage = null) }

    fun submit() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            update { copy(errorMessage = "Email and password are required.") }
            return
        }
        if (state.isSignUp) {
            if (state.name.isBlank()) {
                update { copy(errorMessage = "Name is required for registration.") }
                return
            }
            if (state.password.length < 6) {
                update { copy(errorMessage = "Password must be at least 6 characters.") }
                return
            }
            if (state.password != state.confirmPassword) {
                update { copy(errorMessage = "Passwords do not match.") }
                return
            }
        }

        viewModelScope.launch {
            update { copy(isLoading = true, errorMessage = null) }
            val result = if (_uiState.value.isSignUp) {
                repository.signUp(_uiState.value.email, _uiState.value.password, _uiState.value.name)
            } else {
                repository.signIn(_uiState.value.email, _uiState.value.password)
            }
            result.onSuccess { user ->
                update {
                    copy(
                        isLoading = false,
                        currentUser = user,
                        name = "",
                        cachedEmail = user.email.orEmpty(),
                        cachedDisplayName = user.displayName.orEmpty(),
                        password = "",
                        confirmPassword = "",
                        errorMessage = null,
                        isAdmin = user.email.equals(adminEmail, ignoreCase = true)
                    )
                }
            }.onFailure {
                update {
                    copy(
                        isLoading = false,
                        errorMessage = it.message ?: "Authentication failed."
                    )
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            update { copy(isLoading = true, errorMessage = null) }
            repository.signInWithGoogle(idToken)
                .onFailure {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = it.message ?: "Google sign-in failed."
                        )
                    }
                }
        }
    }

    fun reportGoogleError(message: String) {
        update {
            copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }

    fun signOut() {
        repository.signOut()
        update {
            copy(
                currentUser = null,
                cachedEmail = "",
                cachedDisplayName = "",
                isAdmin = false,
                name = "",
                password = "",
                confirmPassword = "",
                errorMessage = null
            )
        }
    }

    private fun update(block: AuthUiState.() -> AuthUiState) {
        _uiState.value = _uiState.value.block()
    }

    override fun onCleared() {
        repository.removeAuthStateListener(authStateListener)
        super.onCleared()
    }
}

class AuthViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(AuthRepository(context)) as T
    }
}

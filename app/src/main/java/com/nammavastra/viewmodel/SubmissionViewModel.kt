package com.nammavastra.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammavastra.model.StorySubmission
import com.nammavastra.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubmissionUiState(
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

class SubmissionViewModel(
    private val repository: StoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SubmissionUiState())
    val uiState: StateFlow<SubmissionUiState> = _uiState.asStateFlow()

    fun submit(
        submission: StorySubmission,
        imageUri: Uri,
        contentResolver: ContentResolver,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = SubmissionUiState(isSubmitting = true)
            runCatching {
                repository.submitStorySubmission(submission, imageUri, contentResolver)
            }.onSuccess {
                _uiState.value = SubmissionUiState()
                onSuccess()
            }.onFailure {
                _uiState.value = SubmissionUiState(
                    isSubmitting = false,
                    errorMessage = it.message ?: "Unable to submit story."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

class SubmissionViewModelFactory(
    private val repository: StoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SubmissionViewModel(repository) as T
    }
}

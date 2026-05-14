package com.nammavastra.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammavastra.model.Saree
import com.nammavastra.model.UiState
import com.nammavastra.repository.GalleryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val repository: GalleryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState(isLoading = true, data = emptyList<Saree>()))
    val uiState: StateFlow<UiState<List<Saree>>> = _uiState.asStateFlow()

    private val _uploading = MutableStateFlow(false)
    val uploading: StateFlow<Boolean> = _uploading.asStateFlow()

    fun startObserving() {
        refresh()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.fetchSarees()
                .onSuccess { sarees ->
                    _uiState.value = UiState(data = sarees, isLoading = false)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "Unable to load gallery."
                    )
                }
        }
    }

    suspend fun getSaree(id: String): Saree? = repository.getSareeById(id)

    fun uploadSaree(
        saree: Saree,
        imageUri: Uri,
        contentResolver: ContentResolver,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uploading.value = true
            runCatching {
                repository.uploadSaree(saree, imageUri, contentResolver)
            }.onSuccess {
                _uploading.value = false
                refresh()
                onSuccess()
            }.onFailure {
                _uploading.value = false
                onError(it.message ?: "Upload failed.")
            }
        }
    }
}

class GalleryViewModelFactory(
    private val repository: GalleryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GalleryViewModel(repository) as T
    }
}

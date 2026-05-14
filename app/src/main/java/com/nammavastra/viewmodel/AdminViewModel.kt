package com.nammavastra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammavastra.model.Saree
import com.nammavastra.model.StorySubmission
import com.nammavastra.model.WeaverProfile
import com.nammavastra.model.WeaverStory
import com.nammavastra.repository.GalleryRepository
import com.nammavastra.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val submissions: List<StorySubmission> = emptyList(),
    val sarees: List<Saree> = emptyList(),
    val publishedStories: List<WeaverStory> = emptyList(),
    val publishedWeavers: List<WeaverProfile> = emptyList()
)

class AdminViewModel(
    private val storyRepository: StoryRepository,
    private val galleryRepository: GalleryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val submissions = storyRepository.fetchPendingSubmissions().getOrDefault(emptyList())
            val sarees = galleryRepository.fetchSarees().getOrDefault(emptyList())
            val publishedStories = storyRepository.fetchStories().getOrDefault(emptyList())
            val publishedWeavers = storyRepository.fetchWeavers().getOrDefault(emptyList())
            _uiState.value = AdminUiState(
                isLoading = false,
                submissions = submissions,
                sarees = sarees,
                publishedStories = publishedStories,
                publishedWeavers = publishedWeavers
            )
        }
    }

    fun approveSubmission(submission: StorySubmission) {
        viewModelScope.launch {
            runCatching { storyRepository.approveSubmission(submission) }
                .onSuccess { refresh() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(errorMessage = it.message ?: "Approval failed.")
                }
        }
    }

    fun rejectSubmission(id: String) {
        viewModelScope.launch {
            runCatching { storyRepository.rejectSubmission(id) }
                .onSuccess { refresh() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(errorMessage = it.message ?: "Rejection failed.")
                }
        }
    }

    fun removeSaree(saree: Saree) {
        viewModelScope.launch {
            runCatching { galleryRepository.deleteSaree(saree) }
                .onSuccess { refresh() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(errorMessage = it.message ?: "Unable to remove saree.")
                }
        }
    }

    fun removeStory(id: String) {
        viewModelScope.launch {
            runCatching { storyRepository.deleteStory(id) }
                .onSuccess { refresh() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(errorMessage = it.message ?: "Unable to remove story.")
                }
        }
    }

    fun removeWeaver(id: String) {
        viewModelScope.launch {
            runCatching { storyRepository.deleteWeaver(id) }
                .onSuccess { refresh() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(errorMessage = it.message ?: "Unable to remove weaver.")
                }
        }
    }
}

class AdminViewModelFactory(
    private val storyRepository: StoryRepository,
    private val galleryRepository: GalleryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel(storyRepository, galleryRepository) as T
    }
}

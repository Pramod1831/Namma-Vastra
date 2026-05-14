package com.nammavastra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammavastra.model.UiState
import com.nammavastra.model.WeaverProfile
import com.nammavastra.model.WeaverStory
import com.nammavastra.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoryScreenState(
    val storiesState: UiState<List<WeaverStory>> = UiState(isLoading = true, data = emptyList()),
    val weavers: List<WeaverProfile> = emptyList()
)

class StoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StoryScreenState())
    val uiState: StateFlow<StoryScreenState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                storiesState = _uiState.value.storiesState.copy(isLoading = true, errorMessage = null),
                weavers = emptyList()
            )
            val weavers = repository.fetchWeavers().getOrDefault(repository.defaultWeavers())
            repository.fetchStories()
                .onSuccess {
                    _uiState.value = StoryScreenState(
                        storiesState = UiState(data = it, isLoading = false),
                        weavers = weavers
                    )
                }
                .onFailure {
                    _uiState.value = StoryScreenState(
                        storiesState = UiState(
                            data = emptyList(),
                            isLoading = false,
                            errorMessage = it.message ?: "Unable to load stories."
                        ),
                        weavers = weavers
                    )
                }
        }
    }
}

class StoryViewModelFactory(
    private val repository: StoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StoryViewModel(repository) as T
    }
}

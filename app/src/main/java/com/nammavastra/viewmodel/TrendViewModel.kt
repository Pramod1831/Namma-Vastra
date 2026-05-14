package com.nammavastra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammavastra.model.Trend
import com.nammavastra.model.UiState
import com.nammavastra.repository.TrendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrendViewModel(
    private val repository: TrendRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState(isLoading = true, data = emptyList<Trend>()))
    val uiState: StateFlow<UiState<List<Trend>>> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.fetchTrends()
                .onSuccess { trends ->
                    val enriched = trends.map { trend ->
                        trend.copy(description = repository.enrichTrendDescription(trend))
                    }
                    _uiState.value = UiState(data = enriched, isLoading = false)
                }
                .onFailure {
                    _uiState.value = UiState(
                        data = emptyList(),
                        isLoading = false,
                        errorMessage = it.message ?: "Unable to load trends."
                    )
                }
        }
    }
}

class TrendViewModelFactory(
    private val repository: TrendRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrendViewModel(repository) as T
    }
}

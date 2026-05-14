package com.nammavastra.model

data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T,
    val errorMessage: String? = null
)

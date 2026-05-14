package com.nammavastra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammavastra.service.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PriceUiState(
    val materialCost: String = "",
    val zariCost: String = "",
    val labourHours: String = "",
    val overhead: String = "",
    val fabricType: String = "Silk",
    val suggestedPrice: Double? = null,
    val rationale: String = "",
    val isLoading: Boolean = false
)

class PriceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PriceUiState())
    val uiState: StateFlow<PriceUiState> = _uiState.asStateFlow()

    fun updateMaterialCost(value: String) = update { copy(materialCost = value) }
    fun updateZariCost(value: String) = update { copy(zariCost = value) }
    fun updateLabourHours(value: String) = update { copy(labourHours = value) }
    fun updateOverhead(value: String) = update { copy(overhead = value) }
    fun updateFabricType(value: String) = update { copy(fabricType = value) }
    fun reset() {
        _uiState.value = PriceUiState()
    }

    fun calculatePrice() {
        val material = _uiState.value.materialCost.toDoubleOrNull() ?: 0.0
        val zari = _uiState.value.zariCost.toDoubleOrNull() ?: 0.0
        val hours = _uiState.value.labourHours.toDoubleOrNull() ?: 0.0
        val overhead = _uiState.value.overhead.toDoubleOrNull() ?: 0.0
        val labour = hours * 150.0
        val multiplier = if (_uiState.value.fabricType == "Silk") 2.5 else 2.0
        val price = (material + zari + labour + overhead) * multiplier

        _uiState.value = _uiState.value.copy(
            suggestedPrice = price,
            isLoading = true
        )

        viewModelScope.launch {
            val rationale = GeminiService.generate(
                """
                A weaver used Rs.${material + zari} in materials, $hours labour hours, and Rs.$overhead overhead
                to create a ${_uiState.value.fabricType} saree. The suggested retail price is Rs.$price.
                In 2 sentences, explain why this price is fair and competitive for boutique markets in India.
                """.trimIndent()
            ).ifBlank {
                "This price covers raw material risk, weaving time, and boutique-ready margin while protecting artisan earnings."
            }
            _uiState.value = _uiState.value.copy(
                suggestedPrice = price,
                rationale = rationale,
                isLoading = false
            )
        }
    }

    private fun update(block: PriceUiState.() -> PriceUiState) {
        _uiState.value = _uiState.value.block()
    }
}

class PriceViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PriceViewModel() as T
    }
}

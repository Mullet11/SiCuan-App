package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicuan.domain.model.Plan
import com.example.sicuan.domain.usecase.plan.PlanUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlanUiState(
    val plans: List<Plan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class PlanViewModel(
    private val planUseCases: PlanUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    init {
        getPlans()
    }

    private fun getPlans() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            planUseCases.getPlans()
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Terjadi kesalahan saat memuat rencana"
                        )
                    }
                }
                .collect { plans ->
                    _uiState.update { 
                        it.copy(
                            plans = plans,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun addPlan(
        title: String,
        targetAmountText: String,
        deadlineDateMillis: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val cleanedAmount = targetAmountText.replace("[^0-9]".toRegex(), "")
                val targetAmount = cleanedAmount.toDoubleOrNull() ?: 0.0

                if (title.isBlank()) {
                    _uiState.update { it.copy(errorMessage = "Nama target tidak boleh kosong") }
                    return@launch
                }

                if (targetAmount <= 0) {
                    _uiState.update { it.copy(errorMessage = "Target nominal harus lebih dari 0") }
                    return@launch
                }

                if (deadlineDateMillis <= 0) {
                    _uiState.update { it.copy(errorMessage = "Pilih tanggal target tercapai") }
                    return@launch
                }

                val plan = Plan(
                    title = title,
                    targetAmount = targetAmount,
                    deadlineDateMillis = deadlineDateMillis
                )

                planUseCases.addPlan(plan)
                _uiState.update { it.copy(successMessage = "Target berhasil ditambahkan") }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal menambahkan target") }
            }
        }
    }

    fun deletePlan(plan: Plan) {
        viewModelScope.launch {
            try {
                planUseCases.deletePlan(plan)
                _uiState.update { it.copy(successMessage = "Target berhasil dihapus") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal menghapus target") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}

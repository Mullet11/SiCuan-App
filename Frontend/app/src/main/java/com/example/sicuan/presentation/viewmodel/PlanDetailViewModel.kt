package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicuan.domain.model.Plan
import com.example.sicuan.domain.model.PlanHistory
import com.example.sicuan.domain.usecase.plan.PlanUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlanDetailUiState(
    val plan: Plan? = null,
    val histories: List<PlanHistory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class PlanDetailViewModel(
    private val planId: Int,
    private val planUseCases: PlanUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanDetailUiState())
    val uiState: StateFlow<PlanDetailUiState> = _uiState.asStateFlow()

    init {
        loadPlanData()
    }

    private fun loadPlanData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            planUseCases.getPlans()
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Terjadi kesalahan"
                        )
                    }
                }
                .collect { plans ->
                    val plan = plans.find { it.id == planId }
                    if (plan != null) {
                        _uiState.update { it.copy(plan = plan, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(errorMessage = "Target tidak ditemukan", isLoading = false) }
                    }
                }
        }

        viewModelScope.launch {
            planUseCases.getPlanHistory(planId)
                .catch { /* handle error */ }
                .collect { histories ->
                    _uiState.update { it.copy(histories = histories) }
                }
        }
    }

    fun topUpPlan(amountText: String) {
        val amount = amountText.replace("[^0-9]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val plan = _uiState.value.plan ?: return

        viewModelScope.launch {
            try {
                planUseCases.topUpPlan(plan, amount)
                _uiState.update { it.copy(successMessage = "Berhasil menambah saldo target") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal menambah saldo") }
            }
        }
    }

    fun withdrawPlan(amountText: String) {
        val amount = amountText.replace("[^0-9]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val plan = _uiState.value.plan ?: return

        viewModelScope.launch {
            try {
                planUseCases.withdrawPlan(plan, amount)
                _uiState.update { it.copy(successMessage = "Berhasil menarik saldo") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Gagal menarik saldo") }
            }
        }
    }

    fun deletePlan() {
        val plan = _uiState.value.plan ?: return
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

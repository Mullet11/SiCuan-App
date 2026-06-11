package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicuan.domain.model.Budget
import com.example.sicuan.domain.usecase.budget.BudgetUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val budgetUseCases: BudgetUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeBudgets()
    }

    private fun observeBudgets() {
        viewModelScope.launch {
            budgetUseCases.getAllBudgets()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat data budget"
                        )
                    }
                }
                .collect { budgets ->
                    _uiState.update {
                        it.copy(
                            budgets = budgets,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun addBudget(
        category: String,
        limitAmountText: String
    ) {
        val limitAmount = limitAmountText.toDoubleOrNull()

        if (category.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Kategori budget tidak boleh kosong")
            }
            return
        }

        if (limitAmount == null || limitAmount <= 0.0) {
            _uiState.update {
                it.copy(errorMessage = "Nominal budget tidak valid")
            }
            return
        }

        val isDuplicateCategory = _uiState.value.budgets.any { budget ->
            budget.category.equals(category, ignoreCase = true)
        }

        if (isDuplicateCategory) {
            _uiState.update {
                it.copy(errorMessage = "Budget untuk kategori $category sudah ada")
            }
            return
        }

        viewModelScope.launch {
            try {
                val budget = Budget(
                    category = category,
                    limitAmount = limitAmount
                )

                budgetUseCases.addBudget(budget)

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Budget berhasil ditambahkan"
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Gagal menambahkan budget")
                }
            }
        }
    }

    fun updateBudget(
        budgetId: Int,
        category: String,
        limitAmountText: String
    ) {
        val limitAmount = limitAmountText.toDoubleOrNull()

        if (category.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Kategori budget tidak boleh kosong")
            }
            return
        }

        if (limitAmount == null || limitAmount <= 0.0) {
            _uiState.update {
                it.copy(errorMessage = "Nominal budget tidak valid")
            }
            return
        }

        val currentBudget = _uiState.value.budgets.find { budget ->
            budget.id == budgetId
        }

        if (currentBudget == null) {
            _uiState.update {
                it.copy(errorMessage = "Budget tidak ditemukan")
            }
            return
        }

        val isDuplicateCategory = _uiState.value.budgets.any { budget ->
            budget.id != budgetId && budget.category.equals(category, ignoreCase = true)
        }

        if (isDuplicateCategory) {
            _uiState.update {
                it.copy(errorMessage = "Budget untuk kategori $category sudah ada")
            }
            return
        }

        viewModelScope.launch {
            try {
                val updatedBudget = currentBudget.copy(
                    category = category,
                    limitAmount = limitAmount,
                    updatedAt = System.currentTimeMillis()
                )

                budgetUseCases.updateBudget(updatedBudget)

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Budget berhasil diperbarui"
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Gagal memperbarui budget")
                }
            }
        }
    }

    fun deleteBudget(budgetId: Int) {
        viewModelScope.launch {
            try {
                budgetUseCases.deleteBudget(budgetId)

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Budget berhasil dihapus"
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Gagal menghapus budget")
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }
}
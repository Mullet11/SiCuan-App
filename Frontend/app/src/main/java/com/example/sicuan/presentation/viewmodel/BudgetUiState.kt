package com.example.sicuan.presentation.viewmodel

import com.example.sicuan.domain.model.Budget

data class BudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
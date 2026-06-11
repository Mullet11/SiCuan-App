package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicuan.domain.usecase.budget.BudgetUseCases

class BudgetViewModelFactory(
    private val budgetUseCases: BudgetUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            return BudgetViewModel(budgetUseCases) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
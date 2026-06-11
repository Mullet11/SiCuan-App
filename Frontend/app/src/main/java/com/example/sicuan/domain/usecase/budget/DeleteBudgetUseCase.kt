package com.example.sicuan.domain.usecase.budget

import com.example.sicuan.domain.repository.BudgetRepository

class DeleteBudgetUseCase(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budgetId: Int) {
        repository.deleteBudgetById(budgetId)
    }
}
package com.example.sicuan.domain.usecase.budget

import com.example.sicuan.domain.model.Budget
import com.example.sicuan.domain.repository.BudgetRepository

class UpdateBudgetUseCase(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) {
        repository.updateBudget(budget)
    }
}
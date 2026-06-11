package com.example.sicuan.domain.usecase.budget

import com.example.sicuan.domain.model.Budget
import com.example.sicuan.domain.repository.BudgetRepository

class AddBudgetUseCase(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) {
        repository.addBudget(budget)
    }
}
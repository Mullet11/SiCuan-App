package com.example.sicuan.domain.usecase.budget

import com.example.sicuan.domain.model.Budget
import com.example.sicuan.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class GetBudgetByIdUseCase(
    private val repository: BudgetRepository
) {
    operator fun invoke(budgetId: Int): Flow<Budget?> {
        return repository.getBudgetById(budgetId)
    }
}
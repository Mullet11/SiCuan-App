package com.example.sicuan.domain.usecase.budget

import com.example.sicuan.domain.model.Budget
import com.example.sicuan.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class GetAllBudgetsUseCase(
    private val repository: BudgetRepository
) {
    operator fun invoke(): Flow<List<Budget>> {
        return repository.getAllBudgets()
    }
}
package com.example.sicuan.domain.usecase.budget

data class BudgetUseCases(
    val getAllBudgets: GetAllBudgetsUseCase,
    val getBudgetById: GetBudgetByIdUseCase,
    val addBudget: AddBudgetUseCase,
    val updateBudget: UpdateBudgetUseCase,
    val deleteBudget: DeleteBudgetUseCase
)
package com.example.sicuan.domain.repository

import com.example.sicuan.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {

    fun getAllBudgets(): Flow<List<Budget>>

    fun getBudgetById(budgetId: Int): Flow<Budget?>

    fun getBudgetByCategory(category: String): Flow<Budget?>

    suspend fun addBudget(budget: Budget)

    suspend fun updateBudget(budget: Budget)

    suspend fun deleteBudgetById(budgetId: Int)
}
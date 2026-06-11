package com.example.sicuan.data.repository

import com.example.sicuan.data.local.dao.BudgetDao
import com.example.sicuan.data.mapper.toDomain
import com.example.sicuan.data.mapper.toEntity
import com.example.sicuan.domain.model.Budget
import com.example.sicuan.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAllBudgets().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getBudgetById(budgetId: Int): Flow<Budget?> {
        return budgetDao.getBudgetById(budgetId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getBudgetByCategory(category: String): Flow<Budget?> {
        return budgetDao.getBudgetByCategory(category).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun addBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(
            budget.copy(
                updatedAt = System.currentTimeMillis()
            ).toEntity()
        )
    }

    override suspend fun deleteBudgetById(budgetId: Int) {
        budgetDao.deleteBudgetById(budgetId)
    }
}
package com.example.sicuan.data.mapper

import com.example.sicuan.data.local.entity.BudgetEntity
import com.example.sicuan.domain.model.Budget

fun BudgetEntity.toDomain(): Budget {
    return Budget(
        id = id,
        category = category,
        limitAmount = limitAmount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        category = category,
        limitAmount = limitAmount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
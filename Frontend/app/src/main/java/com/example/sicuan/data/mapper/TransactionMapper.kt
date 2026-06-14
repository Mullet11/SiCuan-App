package com.example.sicuan.data.mapper

import com.example.sicuan.data.local.entity.TransactionEntity
import com.example.sicuan.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        title = title,
        amount = amount,
        type = type,
        category = category,
        date = date,
        note = note,
        merchant = merchant,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        type = type,
        category = category,
        date = date,
        note = note,
        merchant = merchant,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

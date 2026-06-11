package com.example.sicuan.domain.model

data class Transaction(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: Long,
    val note: String,
    val merchant: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

object TransactionType {
    const val INCOME = "income"
    const val EXPENSE = "expense"
}
package com.example.sicuan.domain.model

data class Budget(
    val id: Int = 0,
    val category: String,
    val limitAmount: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
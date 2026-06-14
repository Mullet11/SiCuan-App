package com.example.sicuan.domain.model

enum class PlanHistoryType {
    TOP_UP,
    WITHDRAW
}

data class PlanHistory(
    val id: Int = 0,
    val planId: Int,
    val amount: Double,
    val type: PlanHistoryType,
    val dateMillis: Long = System.currentTimeMillis()
)

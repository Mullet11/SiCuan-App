package com.example.sicuan.presentation.viewmodel

import com.example.sicuan.domain.model.Transaction

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val prefilledAmount: String? = null,
    val prefilledTitle: String? = null,
    val prefilledNote: String? = null,
    val prefilledMerchant: String? = null,
    val prefilledCategory: String? = null,
    val prefilledDateMillis: Long? = null,
    val exportedReports: List<ReportData> = emptyList()
)

data class ReportData(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val subtitle: String,
    val timestamp: Long,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val transactionCount: Int = 0,
    val topCategories: List<CategorySummary> = emptyList()
)

data class CategorySummary(
    val name: String,
    val amount: Double,
    val percentage: Float
)

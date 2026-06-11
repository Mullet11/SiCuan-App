package com.example.sicuan.presentation.viewmodel

import com.example.sicuan.domain.model.Transaction

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
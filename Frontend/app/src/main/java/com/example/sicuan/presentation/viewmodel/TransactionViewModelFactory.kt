package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicuan.domain.usecase.transaction.TransactionUseCases

class TransactionViewModelFactory(
    private val transactionUseCases: TransactionUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(transactionUseCases) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
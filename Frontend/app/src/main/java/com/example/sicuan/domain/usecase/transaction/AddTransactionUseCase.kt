package com.example.sicuan.domain.usecase.transaction

import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.repository.TransactionRepository

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.addTransaction(transaction)
    }
}

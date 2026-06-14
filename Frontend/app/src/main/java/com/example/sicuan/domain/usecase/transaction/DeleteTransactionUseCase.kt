package com.example.sicuan.domain.usecase.transaction

import com.example.sicuan.domain.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: Int) {
        repository.deleteTransactionById(transactionId)
    }
}
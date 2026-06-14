package com.example.sicuan.domain.usecase.transaction

import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionByIdUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(transactionId: Int): Flow<Transaction?> {
        return repository.getTransactionById(transactionId)
    }
}

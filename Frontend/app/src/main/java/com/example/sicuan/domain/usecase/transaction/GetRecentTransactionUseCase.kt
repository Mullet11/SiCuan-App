package com.example.sicuan.domain.usecase.transaction

import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetRecentTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(limit: Int): Flow<List<Transaction>> {
        return repository.getRecentTransactions(limit)
    }
}
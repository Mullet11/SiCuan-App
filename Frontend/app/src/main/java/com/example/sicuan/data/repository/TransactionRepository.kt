package com.example.sicuan.domain.repository

import com.example.sicuan.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTransactionById(transactionId: Int): Flow<Transaction?>

    suspend fun addTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun deleteTransactionById(transactionId: Int)

    fun getTotalAmountByType(type: String): Flow<Double>

    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>
}

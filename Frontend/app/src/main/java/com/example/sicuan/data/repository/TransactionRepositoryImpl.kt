package com.example.sicuan.data.repository

import com.example.sicuan.data.local.dao.TransactionDao
import com.example.sicuan.data.mapper.toDomain
import com.example.sicuan.data.mapper.toEntity
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getTransactionById(transactionId: Int): Flow<Transaction?> {
        return transactionDao.getTransactionById(transactionId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(
            transaction.copy(
                updatedAt = System.currentTimeMillis()
            ).toEntity()
        )
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransactionById(transactionId: Int) {
        transactionDao.deleteTransactionById(transactionId)
    }

    override fun getTotalAmountByType(type: String): Flow<Double> {
        return transactionDao.getTotalAmountByType(type)
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }
}
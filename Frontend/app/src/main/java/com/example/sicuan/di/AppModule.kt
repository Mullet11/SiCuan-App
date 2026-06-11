package com.example.sicuan.di

import android.content.Context
import com.example.sicuan.data.local.database.SiCuanDatabase
import com.example.sicuan.data.repository.TransactionRepositoryImpl
import com.example.sicuan.domain.repository.TransactionRepository
import com.example.sicuan.domain.usecase.transaction.AddTransactionUseCase
import com.example.sicuan.domain.usecase.transaction.DeleteTransactionUseCase
import com.example.sicuan.domain.usecase.transaction.GetAllTransactionsUseCase
import com.example.sicuan.domain.usecase.transaction.GetRecentTransactionsUseCase
import com.example.sicuan.domain.usecase.transaction.GetTotalAmountByTypeUseCase
import com.example.sicuan.domain.usecase.transaction.GetTransactionByIdUseCase
import com.example.sicuan.domain.usecase.transaction.TransactionUseCases
import com.example.sicuan.domain.usecase.transaction.UpdateTransactionUseCase

object AppModule {

    fun provideDatabase(context: Context): SiCuanDatabase {
        return SiCuanDatabase.getDatabase(context)
    }

    fun provideTransactionRepository(context: Context): TransactionRepository {
        val database = provideDatabase(context)

        return TransactionRepositoryImpl(
            transactionDao = database.transactionDao()
        )
    }

    fun provideTransactionUseCases(context: Context): TransactionUseCases {
        val repository = provideTransactionRepository(context)

        return TransactionUseCases(
            getAllTransactions = GetAllTransactionsUseCase(repository),
            getTransactionById = GetTransactionByIdUseCase(repository),
            addTransaction = AddTransactionUseCase(repository),
            updateTransaction = UpdateTransactionUseCase(repository),
            deleteTransaction = DeleteTransactionUseCase(repository),
            getTotalAmountByType = GetTotalAmountByTypeUseCase(repository),
            getRecentTransactions = GetRecentTransactionsUseCase(repository)
        )
    }
}
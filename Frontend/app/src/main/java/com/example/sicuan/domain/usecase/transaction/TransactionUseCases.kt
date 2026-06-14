package com.example.sicuan.domain.usecase.transaction

data class TransactionUseCases(
    val getAllTransactions: GetAllTransactionsUseCase,
    val getTransactionById: GetTransactionByIdUseCase,
    val addTransaction: AddTransactionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    val deleteTransaction: DeleteTransactionUseCase,
    val getTotalAmountByType: GetTotalAmountByTypeUseCase,
    val getRecentTransactions: GetRecentTransactionsUseCase
)

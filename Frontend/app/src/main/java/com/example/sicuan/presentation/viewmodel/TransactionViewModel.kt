package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.domain.usecase.transaction.TransactionUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionUseCases: TransactionUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState = _uiState.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction = _selectedTransaction.asStateFlow()

    private var selectedTransactionJob: Job? = null

    init {
        observeTransactionSummary()
    }

    private fun observeTransactionSummary() {
        viewModelScope.launch {
            combine(
                transactionUseCases.getAllTransactions(),
                transactionUseCases.getTotalAmountByType(TransactionType.INCOME),
                transactionUseCases.getTotalAmountByType(TransactionType.EXPENSE)
            ) { transactions, totalIncome, totalExpense ->
                TransactionUiState(
                    transactions = transactions,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = totalIncome - totalExpense,
                    isLoading = false
                )
            }.catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal memuat data transaksi"
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun observeTransactionById(transactionId: Int) {
        selectedTransactionJob?.cancel()

        selectedTransactionJob = viewModelScope.launch {
            transactionUseCases.getTransactionById(transactionId)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Gagal memuat detail transaksi"
                        )
                    }
                }
                .collect { transaction ->
                    _selectedTransaction.value = transaction
                }
        }
    }

    fun addTransaction(
        title: String,
        amountText: String,
        type: String = TransactionType.EXPENSE,
        category: String = "Umum",
        note: String = "",
        merchant: String? = null,
        dateMillis: Long = System.currentTimeMillis(),
        onSuccess: () -> Unit = {}
    ) {
        val amount = amountText.toDoubleOrNull()

        if (title.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Nama transaksi tidak boleh kosong")
            }
            return
        }

        if (amount == null || amount <= 0.0) {
            _uiState.update {
                it.copy(errorMessage = "Nominal transaksi tidak valid")
            }
            return
        }

        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    title = title.trim(),
                    amount = amount,
                    type = type,
                    category = category.ifBlank { "Umum" },
                    date = dateMillis,
                    note = note.trim(),
                    merchant = merchant
                )

                transactionUseCases.addTransaction(transaction)

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Transaksi berhasil ditambahkan"
                    )
                }

                onSuccess()
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Gagal menambahkan transaksi"
                    )
                }
            }
        }
    }

    fun updateSelectedTransaction(
        title: String,
        amountText: String,
        type: String,
        category: String,
        note: String,
        merchant: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        val currentTransaction = _selectedTransaction.value

        if (currentTransaction == null) {
            _uiState.update {
                it.copy(errorMessage = "Transaksi tidak ditemukan")
            }
            return
        }

        val amount = amountText.toDoubleOrNull()

        if (title.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Nama transaksi tidak boleh kosong")
            }
            return
        }

        if (amount == null || amount <= 0.0) {
            _uiState.update {
                it.copy(errorMessage = "Nominal transaksi tidak valid")
            }
            return
        }

        viewModelScope.launch {
            try {
                val updatedTransaction = currentTransaction.copy(
                    title = title.trim(),
                    amount = amount,
                    type = type,
                    category = category.ifBlank { "Umum" },
                    note = note.trim(),
                    merchant = merchant,
                    updatedAt = System.currentTimeMillis()
                )

                transactionUseCases.updateTransaction(updatedTransaction)

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Transaksi berhasil diperbarui"
                    )
                }

                onSuccess()
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Gagal memperbarui transaksi"
                    )
                }
            }
        }
    }

    fun deleteTransaction(
        transactionId: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                transactionUseCases.deleteTransaction(transactionId)

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Transaksi berhasil dihapus"
                    )
                }

                onSuccess()
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Gagal menghapus transaksi"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun exportReport() {
        val currentDate = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("id", "ID")).format(java.util.Date())
        val state = _uiState.value

        // Build category summaries from expense transactions
        val expenseTransactions = state.transactions.filter { it.type == com.example.sicuan.domain.model.TransactionType.EXPENSE }
        val totalExp = state.totalExpense.coerceAtLeast(1.0)
        val categoryMap = expenseTransactions.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { (cat, amt) ->
                CategorySummary(
                    name = cat,
                    amount = amt,
                    percentage = ((amt / totalExp) * 100).toFloat()
                )
            }

        val newReport = ReportData(
            id = System.currentTimeMillis(),
            title = "Laporan Keuangan",
            subtitle = "$currentDate | PDF & CSV",
            timestamp = System.currentTimeMillis(),
            totalIncome = state.totalIncome,
            totalExpense = state.totalExpense,
            balance = state.balance,
            transactionCount = state.transactions.size,
            topCategories = categoryMap
        )
        _uiState.update {
            it.copy(
                exportedReports = listOf(newReport) + it.exportedReports,
                successMessage = "Laporan berhasil ditambahkan"
            )
        }
    }

    fun deleteReport(reportId: Long) {
        _uiState.update {
            it.copy(
                exportedReports = it.exportedReports.filter { r -> r.id != reportId },
                successMessage = "Laporan berhasil dihapus"
            )
        }
    }

    fun setPrefilledData(
        amount: String? = null,
        title: String? = null,
        note: String? = null,
        merchant: String? = null,
        category: String? = null,
        dateMillis: Long? = null
    ) {
        _uiState.update {
            it.copy(
                prefilledAmount = amount,
                prefilledTitle = title,
                prefilledNote = note,
                prefilledMerchant = merchant,
                prefilledCategory = category,
                prefilledDateMillis = dateMillis
            )
        }
    }
}
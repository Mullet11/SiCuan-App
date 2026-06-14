package com.example.sicuan.presentation.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanEmptyState
import com.example.sicuan.presentation.component.SiCuanErrorState
import com.example.sicuan.presentation.component.SiCuanLoadingState
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanTextField
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.NumberFormat
import java.util.Locale

private const val FILTER_ALL = "all"
private const val FILTER_EXPENSE = "expense"
private const val FILTER_INCOME = "income"

@Composable
fun TransactionListScreen(
    transactions: List<Transaction>,
    isLoading: Boolean,
    errorMessage: String?,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToDetailTransaction: (Int) -> Unit
) {
    val selectedFilter = rememberSaveable { mutableStateOf(FILTER_ALL) }
    val searchQuery = rememberSaveable { mutableStateOf("") }

    val filteredByType = when (selectedFilter.value) {
        FILTER_EXPENSE -> transactions.filter { it.type == TransactionType.EXPENSE }
        FILTER_INCOME -> transactions.filter { it.type == TransactionType.INCOME }
        else -> transactions
    }

    val filteredTransactions = if (searchQuery.value.isBlank()) {
        filteredByType
    } else {
        val query = searchQuery.value.trim().lowercase()

        filteredByType.filter { transaction ->
            transaction.title.lowercase().contains(query) ||
                    transaction.category.lowercase().contains(query) ||
                    transaction.note.lowercase().contains(query) ||
                    transaction.merchant.orEmpty().lowercase().contains(query)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Daftar Transaksi",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SiCuanPrimaryButton(
            text = "Tambah Transaksi",
            onClick = onNavigateToAddTransaction
        )

        SiCuanTextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
            },
            label = "Cari Transaksi",
            placeholder = "Cari nama, kategori, merchant, atau catatan"
        )

        TransactionFilterChips(
            selectedFilter = selectedFilter.value,
            onFilterSelected = {
                selectedFilter.value = it
            }
        )

        when {
            isLoading -> {
                SiCuanLoadingState(
                    message = "Memuat transaksi..."
                )
            }

            errorMessage != null -> {
                SiCuanErrorState(
                    title = "Gagal Memuat Transaksi",
                    message = errorMessage
                )
            }

            transactions.isEmpty() -> {
                SiCuanEmptyState(
                    title = "Belum ada transaksi",
                    message = "Tekan tombol Tambah Transaksi untuk mulai mencatat keuanganmu.",
                    actionText = "Tambah Transaksi",
                    onActionClick = onNavigateToAddTransaction
                )
            }

            filteredByType.isEmpty() -> {
                val filterLabel = when (selectedFilter.value) {
                    FILTER_EXPENSE -> "pengeluaran"
                    FILTER_INCOME -> "pemasukan"
                    else -> "transaksi"
                }

                SiCuanEmptyState(
                    title = "Tidak ada $filterLabel",
                    message = "Belum ada data $filterLabel yang tersimpan di database lokal."
                )
            }

            filteredTransactions.isEmpty() -> {
                SiCuanEmptyState(
                    title = "Transaksi tidak ditemukan",
                    message = "Tidak ada transaksi yang cocok dengan kata kunci \"${searchQuery.value}\"."
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
                ) {
                    items(
                        items = filteredTransactions,
                        key = { transaction -> transaction.id }
                    ) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = {
                                onNavigateToDetailTransaction(transaction.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionFilterChips(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
    ) {
        Text(
            text = "Filter Transaksi",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
        ) {
            FilterChip(
                selected = selectedFilter == FILTER_ALL,
                onClick = {
                    onFilterSelected(FILTER_ALL)
                },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                label = {
                    Text(text = "Semua")
                }
            )

            FilterChip(
                selected = selectedFilter == FILTER_EXPENSE,
                onClick = {
                    onFilterSelected(FILTER_EXPENSE)
                },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.error,
                    selectedLabelColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                label = {
                    Text(text = "Pengeluaran")
                }
            )

            FilterChip(
                selected = selectedFilter == FILTER_INCOME,
                onClick = {
                    onFilterSelected(FILTER_INCOME)
                },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                label = {
                    Text(text = "Pemasukan")
                }
            )
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val amountColor = if (transaction.type == TransactionType.EXPENSE) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    val typeLabel = if (transaction.type == TransactionType.EXPENSE) {
        "Pengeluaran"
    } else {
        "Pemasukan"
    }

    val amountPrefix = if (transaction.type == TransactionType.EXPENSE) {
        "-"
    } else {
        "+"
    }

    SiCuanCard(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Text(
            text = transaction.title,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "$amountPrefix${formatCurrency(transaction.amount)}",
            style = MaterialTheme.typography.headlineMedium,
            color = amountColor
        )

        Text(
            text = "$typeLabel • ${transaction.category}",
            style = MaterialTheme.typography.bodyMedium
        )

        if (!transaction.merchant.isNullOrBlank()) {
            Text(
                text = "Merchant: ${transaction.merchant}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (transaction.note.isNotBlank()) {
            Text(
                text = transaction.note,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Klik untuk melihat detail",
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace(",00", "")
}
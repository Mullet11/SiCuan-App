package com.example.sicuan.presentation.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionListScreen(
    transactions: List<Transaction>,
    isLoading: Boolean,
    errorMessage: String?,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToDetailTransaction: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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

        when {
            isLoading -> {
                Text(
                    text = "Memuat transaksi...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            transactions.isEmpty() -> {
                SiCuanCard {
                    Text(
                        text = "Belum ada transaksi.",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Tekan tombol Tambah Transaksi untuk mulai mencatat keuanganmu.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
                ) {
                    items(
                        items = transactions,
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
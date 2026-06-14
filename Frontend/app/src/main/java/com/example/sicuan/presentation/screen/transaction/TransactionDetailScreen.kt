package com.example.sicuan.presentation.screen.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.background

@Composable
fun TransactionDetailScreen(
    transactionId: Int,
    transaction: Transaction?,
    onNavigateToEdit: (Int) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Detail Transaksi",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (transaction == null) {
            SiCuanCard {
                Text(
                    text = "Transaksi tidak ditemukan.",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "ID Transaksi: $transactionId",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SiCuanSecondaryButton(
                text = "Kembali",
                onClick = onBack
            )
        } else {
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

            val amountColor = if (transaction.type == TransactionType.EXPENSE) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }

            SiCuanCard {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "$amountPrefix${formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = amountColor
                )

                Text(
                    text = "Jenis: $typeLabel",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Kategori: ${transaction.category}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Tanggal: ${formatDate(transaction.date)}",
                    style = MaterialTheme.typography.bodyLarge
                )

                if (!transaction.merchant.isNullOrBlank()) {
                    Text(
                        text = "Merchant: ${transaction.merchant}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (transaction.note.isNotBlank()) {
                    Text(
                        text = "Catatan: ${transaction.note}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            SiCuanPrimaryButton(
                text = "Edit Transaksi",
                onClick = {
                    onNavigateToEdit(transaction.id)
                }
            )

            SiCuanSecondaryButton(
                text = "Hapus Transaksi",
                onClick = {
                    showDeleteDialog.value = true
                }
            )

            SiCuanSecondaryButton(
                text = "Kembali",
                onClick = onBack
            )
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog.value = false
            },
            title = {
                Text(text = "Hapus Transaksi?")
            },
            text = {
                Text(
                    text = "Transaksi ini akan dihapus secara permanen dari database lokal. Tindakan ini tidak dapat dibatalkan."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        onDelete()
                    }
                ) {
                    Text(
                        text = "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                    }
                ) {
                    Text(text = "Batal")
                }
            }
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace(",00", "")
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    return formatter.format(Date(timestamp))
}
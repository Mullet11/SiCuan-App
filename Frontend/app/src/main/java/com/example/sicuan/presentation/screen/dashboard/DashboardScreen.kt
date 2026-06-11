package com.example.sicuan.presentation.screen.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    balance: Double,
    totalIncome: Double,
    totalExpense: Double,
    recentTransactions: List<Transaction>,
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransactionDetail: (Int) -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToInsight: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SiCuanCard {
            Text(
                text = "Saldo Saat Ini",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = formatCurrency(balance),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pemasukan",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = formatCurrency(totalIncome),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pengeluaran",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = formatCurrency(totalExpense),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Text(
            text = "Transaksi Terbaru",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (recentTransactions.isEmpty()) {
            SiCuanCard {
                Text(
                    text = "Belum ada transaksi.",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Tambahkan transaksi pertama untuk mulai memantau keuanganmu.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            recentTransactions.forEach { transaction ->
                RecentTransactionItem(
                    transaction = transaction,
                    onClick = {
                        onNavigateToTransactionDetail(transaction.id)
                    }
                )
            }
        }

        SiCuanPrimaryButton(
            text = "Lihat Semua Transaksi",
            onClick = onNavigateToTransactions
        )

        SiCuanSecondaryButton(
            text = "Anggaran",
            onClick = onNavigateToBudget
        )

        SiCuanSecondaryButton(
            text = "Insight API",
            onClick = onNavigateToInsight
        )

        SiCuanSecondaryButton(
            text = "Profil",
            onClick = onNavigateToProfile
        )
    }
}

@Composable
private fun RecentTransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
    val amountColor = if (transaction.type == TransactionType.EXPENSE) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
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
            style = MaterialTheme.typography.titleLarge,
            color = amountColor
        )

        Text(
            text = transaction.category,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace(",00", "")
}
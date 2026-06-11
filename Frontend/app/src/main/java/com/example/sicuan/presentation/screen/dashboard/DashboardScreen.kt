package com.example.sicuan.presentation.screen.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanEmptyState
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
    allTransactions: List<Transaction>,
    recentTransactions: List<Transaction>,
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransactionDetail: (Int) -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToInsight: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val expenseCategorySummaries = allTransactions
        .filter { transaction ->
            transaction.type == TransactionType.EXPENSE
        }
        .groupBy { transaction ->
            transaction.category.ifBlank { "Umum" }
        }
        .map { categoryGroup ->
            CategorySummary(
                category = categoryGroup.key,
                totalAmount = categoryGroup.value.sumOf { transaction ->
                    transaction.amount
                }
            )
        }
        .sortedByDescending { summary ->
            summary.totalAmount
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                color = if (balance < 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
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
            text = "Ringkasan Pengeluaran",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (expenseCategorySummaries.isEmpty()) {
            SiCuanEmptyState(
                title = "Belum ada pengeluaran",
                message = "Tambahkan transaksi pengeluaran untuk melihat kategori pengeluaran terbesar."
            )
        } else {
            SiCuanCard {
                expenseCategorySummaries.forEach { summary ->
                    CategorySummaryItem(
                        summary = summary,
                        totalExpense = totalExpense
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
            SiCuanEmptyState(
                title = "Belum ada transaksi",
                message = "Tambahkan transaksi pertama untuk mulai memantau keuanganmu.",
                actionText = "Tambah Transaksi",
                onActionClick = onNavigateToTransactions
            )
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
private fun CategorySummaryItem(
    summary: CategorySummary,
    totalExpense: Double
) {
    val percentage = if (totalExpense > 0) {
        (summary.totalAmount / totalExpense) * 100
    } else {
        0.0
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SiCuanDimens.SpacingSm),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingXs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = summary.category,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = formatCurrency(summary.totalAmount),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Text(
            text = "${percentage.toInt()}% dari total pengeluaran",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
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

private data class CategorySummary(
    val category: String,
    val totalAmount: Double
)

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace(",00", "")
}
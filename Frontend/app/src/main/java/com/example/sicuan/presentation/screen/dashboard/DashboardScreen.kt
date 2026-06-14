package com.example.sicuan.presentation.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CallMade
import androidx.compose.material.icons.outlined.CallReceived
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.sicuan.domain.model.CurrencyRate
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.*
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.NumberFormat
import java.util.Locale

import com.example.sicuan.domain.model.Plan

@Composable
fun DashboardScreen(
    balance: Double,
    totalIncome: Double,
    totalExpense: Double,
    allTransactions: List<Transaction>,
    recentTransactions: List<Transaction>,
    currencyRates: List<CurrencyRate>,
    plans: List<Plan>,
    userName: String?,
    photoUrl: String?,
    onNavigateToTransactions: () -> Unit,
    onInputClick: (String?) -> Unit,
    onNavigateToTransactionDetail: (Int) -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToInsight: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToEdukasi: () -> Unit
) {
    val expenseCategorySummaries = allTransactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category.ifBlank { "Umum" } }
        .map { CategorySummary(it.key, it.value.sumOf { tx -> tx.amount }) }
        .sortedByDescending { it.totalAmount }

    val expenseRatio = if (totalIncome > 0) (totalExpense / totalIncome) else 0.0
    val analysisScore = when {
        totalIncome == 0.0 && totalExpense == 0.0 -> 0f
        expenseRatio <= 0.4 -> ((1.0 - expenseRatio) * 100).toFloat().coerceIn(0f, 100f)
        expenseRatio <= 0.6 -> ((1.0 - expenseRatio) * 100).toFloat().coerceIn(0f, 100f)
        expenseRatio <= 0.8 -> ((1.0 - expenseRatio) * 100).toFloat().coerceIn(0f, 100f)
        expenseRatio <= 1.0 -> ((1.0 - expenseRatio) * 100).toFloat().coerceIn(0f, 100f)
        else -> (Math.max(0.0, 1.0 - expenseRatio) * 100).toFloat().coerceIn(0f, 100f)
    }
    val analysisPercentage = (analysisScore / 100f).coerceIn(0f, 1f)

    val statusText = when {
        totalIncome == 0.0 && totalExpense == 0.0 -> "Mulai Catat"
        expenseRatio <= 0.4 -> "Sangat Baik"
        expenseRatio <= 0.6 -> "Baik"
        expenseRatio <= 0.8 -> "Cukup Baik"
        expenseRatio <= 1.0 -> "Perlu Perhatian"
        else -> "Buruk"
    }
    val statusColor = when {
        totalIncome == 0.0 && totalExpense == 0.0 -> MaterialTheme.colorScheme.onSurfaceVariant
        expenseRatio <= 0.6 -> MaterialTheme.colorScheme.primary
        expenseRatio <= 0.8 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }

    val now = System.currentTimeMillis()
    val nearestPlan = plans
        .filter { it.deadlineDateMillis > now }
        .minByOrNull { it.deadlineDateMillis }
    val nearestPlanDaysLeft = nearestPlan?.let {
        ((it.deadlineDateMillis - now) / (1000 * 60 * 60 * 24)).toInt()
    } ?: 0

    var showCalculatorSheet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SiCuanTopAppBar(userName = userName, photoUrl = photoUrl)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = SiCuanDimens.SpacingLg,
                end = SiCuanDimens.SpacingLg,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingLg)
        ) {
            item {
                TotalSaldoCard(
                    saldo = formatCurrency(balance),
                    pemasukanBulanIni = "+ ${formatCurrency(totalIncome)}"
                )
            }

            item {
                SiCuanActionButtons(
                    onInputClick = { onInputClick(null) },
                    onCalculatorClick = { showCalculatorSheet = true },
                    onAiClick = onNavigateToAiAssistant,
                    onEdukasiClick = onNavigateToEdukasi
                )
            }

            item {
                AnalisisCuanCard(
                    percentage = analysisPercentage,
                    statusText = statusText,
                    statusColor = statusColor,
                    nearestPlanTitle = nearestPlan?.title,
                    nearestPlanProgress = nearestPlan?.progressPercentage ?: 0f,
                    nearestPlanDaysLeft = nearestPlanDaysLeft,
                    onCardClick = onNavigateToInsight
                )
            }



            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaksi Terakhir",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Lihat Semua",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToTransactions() }
                    )
                }
            }

            if (recentTransactions.isEmpty()) {
                item {
                    SiCuanEmptyState(
                        title = "Belum ada transaksi",
                        message = "Tambahkan transaksi pertama untuk mulai memantau keuanganmu.",
                        actionText = "Tambah Transaksi",
                        onActionClick = onNavigateToTransactions
                    )
                }
            } else {
                items(recentTransactions) { transaction ->
                    RecentTransactionItemStyled(
                        transaction = transaction,
                        onClick = { onNavigateToTransactionDetail(transaction.id) }
                    )
                }
            }

        }

        if (showCalculatorSheet) {
            com.example.sicuan.presentation.screen.calculator.CalculatorBottomSheet(
                rates = currencyRates,
                onDismissRequest = { showCalculatorSheet = false },
                onUseResult = { result ->
                    onInputClick(result)
                }
            )
        }
    }
}

@Composable
private fun RecentTransactionItemStyled(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val amountPrefix = if (isExpense) "-" else "+"
    val amountColor = if (isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val iconContainerColor = if (isExpense) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val iconColor = if (isExpense) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer

    val iconVector = when {
        transaction.category.contains("Makan", ignoreCase = true) -> Icons.Default.Fastfood
        transaction.category.contains("Belanja", ignoreCase = true) -> Icons.Default.ShoppingCart
        transaction.category.contains("Gaji", ignoreCase = true) -> Icons.Default.Payments
        else -> Icons.Default.Receipt
    }

    SiCuanCard(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(SiCuanDimens.SpacingMd))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$amountPrefix${formatCurrency(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = amountColor
            )
        }
    }
}


private data class CategorySummary(val category: String, val totalAmount: Double)

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace(",00", "")
}

@Composable
private fun GoalCard(title: String, currentAmount: Double, targetAmount: Double) {
    val progress = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    SiCuanCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatCurrency(currentAmount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "dari ${formatCurrency(targetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "${(progress * 100).toInt()}% tercapai",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

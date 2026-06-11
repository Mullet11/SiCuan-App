package com.example.sicuan.presentation.screen.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.sicuan.domain.model.Budget
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionCategory
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanEmptyState
import com.example.sicuan.presentation.component.SiCuanErrorState
import com.example.sicuan.presentation.component.SiCuanLoadingState
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTextField
import com.example.sicuan.presentation.component.TransactionCategorySelector
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetScreen(
    budgets: List<Budget>,
    transactions: List<Transaction>,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onClearMessage: () -> Unit,
    onAddBudget: (category: String, limitAmount: String) -> Unit,
    onUpdateBudget: (budgetId: Int, category: String, limitAmount: String) -> Unit,
    onDeleteBudget: (budgetId: Int) -> Unit,
    onBack: () -> Unit
) {
    val selectedCategory = rememberSaveable {
        mutableStateOf(TransactionCategory.getDefaultCategoryByType(TransactionType.EXPENSE))
    }

    val limitAmount = rememberSaveable {
        mutableStateOf("")
    }

    val limitAmountError = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val editingBudgetId = rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val deleteBudgetId = rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val isEditing = editingBudgetId.value != null

    fun resetForm() {
        selectedCategory.value =
            TransactionCategory.getDefaultCategoryByType(TransactionType.EXPENSE)
        limitAmount.value = ""
        limitAmountError.value = null
        editingBudgetId.value = null
        onClearMessage()
    }

    fun validateForm(): Boolean {
        val amountValue = limitAmount.value.toDoubleOrNull()

        val isDuplicateCategory = budgets.any { budget ->
            budget.category.equals(selectedCategory.value, ignoreCase = true) &&
                    budget.id != editingBudgetId.value
        }

        return when {
            limitAmount.value.isBlank() -> {
                limitAmountError.value = "Nominal budget tidak boleh kosong"
                false
            }

            amountValue == null -> {
                limitAmountError.value = "Nominal budget harus berupa angka"
                false
            }

            amountValue <= 0.0 -> {
                limitAmountError.value = "Nominal budget harus lebih dari 0"
                false
            }

            isDuplicateCategory -> {
                limitAmountError.value =
                    "Budget untuk kategori ${selectedCategory.value} sudah ada"
                false
            }

            else -> {
                limitAmountError.value = null
                true
            }
        }
    }

    val budgetToDelete = budgets.find { budget ->
        budget.id == deleteBudgetId.value
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Budget",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!successMessage.isNullOrBlank()) {
            Text(
                text = successMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        SiCuanCard {
            Text(
                text = if (isEditing) "Edit Budget" else "Tambah Budget",
                style = MaterialTheme.typography.titleLarge
            )

            TransactionCategorySelector(
                type = TransactionType.EXPENSE,
                selectedCategory = selectedCategory.value,
                onCategorySelected = {
                    selectedCategory.value = it
                    limitAmountError.value = null
                    onClearMessage()
                }
            )

            SiCuanTextField(
                value = limitAmount.value,
                onValueChange = {
                    limitAmount.value = it
                    limitAmountError.value = null
                    onClearMessage()
                },
                label = "Batas Budget",
                placeholder = "Contoh: 500000",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = limitAmountError.value != null,
                supportingText = limitAmountError.value
            )

            SiCuanPrimaryButton(
                text = if (isEditing) "Simpan Perubahan" else "Tambah Budget",
                onClick = {
                    if (validateForm()) {
                        val currentEditingId = editingBudgetId.value

                        if (currentEditingId == null) {
                            onAddBudget(
                                selectedCategory.value,
                                limitAmount.value
                            )
                        } else {
                            onUpdateBudget(
                                currentEditingId,
                                selectedCategory.value,
                                limitAmount.value
                            )
                        }

                        resetForm()
                    }
                }
            )

            if (isEditing) {
                SiCuanSecondaryButton(
                    text = "Batal Edit",
                    onClick = {
                        resetForm()
                    }
                )
            }
        }

        Text(
            text = "Daftar Budget",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        when {
            isLoading -> {
                SiCuanLoadingState(
                    message = "Memuat budget..."
                )
            }

            errorMessage != null -> {
                SiCuanErrorState(
                    title = "Gagal Memuat Budget",
                    message = errorMessage
                )
            }

            budgets.isEmpty() -> {
                SiCuanEmptyState(
                    title = "Belum ada budget",
                    message = "Tambahkan budget berdasarkan kategori pengeluaran untuk mulai mengatur batas keuanganmu."
                )
            }

            else -> {
                budgets.forEach { budget ->
                    BudgetItem(
                        budget = budget,
                        transactions = transactions,
                        onEditClick = {
                            selectedCategory.value = budget.category
                            limitAmount.value = budget.limitAmount.toLong().toString()
                            limitAmountError.value = null
                            editingBudgetId.value = budget.id
                            onClearMessage()
                        },
                        onDeleteClick = {
                            deleteBudgetId.value = budget.id
                        }
                    )
                }
            }
        }

        SiCuanSecondaryButton(
            text = "Kembali",
            onClick = onBack
        )
    }

    if (budgetToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                deleteBudgetId.value = null
            },
            title = {
                Text(text = "Hapus Budget?")
            },
            text = {
                Text(
                    text = "Budget kategori ${budgetToDelete.category} akan dihapus. Data transaksi tidak akan ikut terhapus."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteBudget(budgetToDelete.id)
                        deleteBudgetId.value = null
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
                        deleteBudgetId.value = null
                    }
                ) {
                    Text(text = "Batal")
                }
            }
        )
    }
}

@Composable
private fun BudgetItem(
    budget: Budget,
    transactions: List<Transaction>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val usedAmount = transactions
        .filter { transaction ->
            transaction.type == TransactionType.EXPENSE &&
                    transaction.category == budget.category
        }
        .sumOf { transaction ->
            transaction.amount
        }

    val remainingAmount = budget.limitAmount - usedAmount

    val progress = if (budget.limitAmount > 0) {
        (usedAmount / budget.limitAmount).coerceIn(0.0, 1.0)
    } else {
        0.0
    }

    val progressPercentage = if (budget.limitAmount > 0) {
        ((usedAmount / budget.limitAmount) * 100).toInt()
    } else {
        0
    }

    val isOverBudget = usedAmount > budget.limitAmount

    SiCuanCard {
        Text(
            text = budget.category,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Limit: ${formatCurrency(budget.limitAmount)}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Terpakai: ${formatCurrency(usedAmount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isOverBudget) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        Text(
            text = if (remainingAmount >= 0) {
                "Sisa: ${formatCurrency(remainingAmount)}"
            } else {
                "Melebihi budget: ${formatCurrency(kotlin.math.abs(remainingAmount))}"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = if (isOverBudget) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        LinearProgressIndicator(
            progress = {
                progress.toFloat()
            },
            modifier = Modifier.fillMaxWidth(),
            color = if (isOverBudget) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            text = "$progressPercentage% digunakan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                SiCuanSecondaryButton(
                    text = "Edit",
                    onClick = onEditClick
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                SiCuanSecondaryButton(
                    text = "Hapus",
                    onClick = onDeleteClick
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace(",00", "")
}
package com.example.sicuan.presentation.screen.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTextField
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun EditTransactionScreen(
    transactionId: Int,
    transaction: Transaction?,
    errorMessage: String?,
    onClearMessage: () -> Unit,
    onUpdateTransaction: (
        title: String,
        amount: String,
        type: String,
        category: String,
        note: String,
        merchant: String?
    ) -> Unit,
    onBack: () -> Unit
) {
    val title = rememberSaveable(transactionId) { mutableStateOf("") }
    val amount = rememberSaveable(transactionId) { mutableStateOf("") }
    val type = rememberSaveable(transactionId) { mutableStateOf(TransactionType.EXPENSE) }
    val category = rememberSaveable(transactionId) { mutableStateOf("") }
    val note = rememberSaveable(transactionId) { mutableStateOf("") }
    val merchant = rememberSaveable(transactionId) { mutableStateOf("") }

    val titleError = rememberSaveable(transactionId) { mutableStateOf<String?>(null) }
    val amountError = rememberSaveable(transactionId) { mutableStateOf<String?>(null) }

    val isFormInitialized = rememberSaveable(transactionId) { mutableStateOf(false) }

    LaunchedEffect(transaction?.id) {
        if (transaction != null && !isFormInitialized.value) {
            title.value = transaction.title
            amount.value = transaction.amount.toLong().toString()
            type.value = transaction.type
            category.value = transaction.category
            note.value = transaction.note
            merchant.value = transaction.merchant.orEmpty()

            isFormInitialized.value = true
        }
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (title.value.isBlank()) {
            titleError.value = "Nama transaksi tidak boleh kosong"
            isValid = false
        } else {
            titleError.value = null
        }

        val amountValue = amount.value.toDoubleOrNull()

        when {
            amount.value.isBlank() -> {
                amountError.value = "Nominal tidak boleh kosong"
                isValid = false
            }

            amountValue == null -> {
                amountError.value = "Nominal harus berupa angka"
                isValid = false
            }

            amountValue <= 0.0 -> {
                amountError.value = "Nominal harus lebih dari 0"
                isValid = false
            }

            else -> {
                amountError.value = null
            }
        }

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Edit Transaksi",
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
            Text(
                text = "ID Transaksi: $transactionId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            SiCuanTextField(
                value = title.value,
                onValueChange = {
                    title.value = it
                    titleError.value = null
                    onClearMessage()
                },
                label = "Nama Transaksi",
                placeholder = "Contoh: Makan siang",
                isError = titleError.value != null,
                supportingText = titleError.value
            )

            SiCuanTextField(
                value = amount.value,
                onValueChange = {
                    amount.value = it
                    amountError.value = null
                    onClearMessage()
                },
                label = "Nominal",
                placeholder = "Contoh: 15000",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = amountError.value != null,
                supportingText = amountError.value
            )

            Text(
                text = "Jenis Transaksi",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
            ) {
                FilterChip(
                    selected = type.value == TransactionType.EXPENSE,
                    onClick = {
                        type.value = TransactionType.EXPENSE
                        onClearMessage()
                    },
                    label = {
                        Text(text = "Pengeluaran")
                    }
                )

                FilterChip(
                    selected = type.value == TransactionType.INCOME,
                    onClick = {
                        type.value = TransactionType.INCOME
                        onClearMessage()
                    },
                    label = {
                        Text(text = "Pemasukan")
                    }
                )
            }

            SiCuanTextField(
                value = category.value,
                onValueChange = {
                    category.value = it
                    onClearMessage()
                },
                label = "Kategori",
                placeholder = "Contoh: Makan, Transportasi, Tugas"
            )

            SiCuanTextField(
                value = merchant.value,
                onValueChange = {
                    merchant.value = it
                    onClearMessage()
                },
                label = "Merchant",
                placeholder = "Contoh: Warung Bu Ani"
            )

            SiCuanTextField(
                value = note.value,
                onValueChange = {
                    note.value = it
                    onClearMessage()
                },
                label = "Catatan",
                placeholder = "Catatan tambahan",
                singleLine = false
            )

            SiCuanPrimaryButton(
                text = "Simpan Perubahan",
                onClick = {
                    if (validateForm()) {
                        onUpdateTransaction(
                            title.value,
                            amount.value,
                            type.value,
                            category.value,
                            note.value,
                            merchant.value.ifBlank { null }
                        )
                    }
                }
            )

            SiCuanSecondaryButton(
                text = "Kembali",
                onClick = onBack
            )
        }
    }
}
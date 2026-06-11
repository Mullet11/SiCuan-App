package com.example.sicuan.presentation.screen.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTextField
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun AddTransactionScreen(
    onBack: () -> Unit
) {
    val title = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Tambah Transaksi",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SiCuanTextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = "Nama Transaksi",
            placeholder = "Contoh: Makan siang"
        )

        SiCuanTextField(
            value = amount.value,
            onValueChange = { amount.value = it },
            label = "Nominal",
            placeholder = "Contoh: 15000",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        SiCuanPrimaryButton(
            text = "Simpan",
            onClick = onBack
        )

        SiCuanSecondaryButton(
            text = "Kembali",
            onClick = onBack
        )
    }
}
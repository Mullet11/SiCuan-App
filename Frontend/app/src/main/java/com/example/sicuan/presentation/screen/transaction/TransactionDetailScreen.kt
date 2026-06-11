package com.example.sicuan.presentation.screen.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun TransactionDetailScreen(
    transactionId: Int,
    onNavigateToEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Detail Transaksi",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SiCuanCard {
            Text(text = "ID Transaksi: $transactionId")
            Text(text = "Nama: Contoh Transaksi")
            Text(text = "Nominal: Rp0")
            Text(text = "Kategori: Belum ada")
            Text(text = "Catatan: Data dummy sementara")
        }

        SiCuanPrimaryButton(
            text = "Edit Transaksi",
            onClick = {
                onNavigateToEdit(transactionId)
            }
        )

        SiCuanSecondaryButton(
            text = "Kembali",
            onClick = onBack
        )
    }
}
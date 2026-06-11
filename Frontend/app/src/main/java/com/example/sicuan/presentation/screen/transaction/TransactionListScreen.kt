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
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun TransactionListScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToDetailTransaction: (Int) -> Unit
) {
    val dummyTransactions = listOf(
        1 to "Makan siang - Rp15.000",
        2 to "Print tugas - Rp5.000",
        3 to "Uang masuk - Rp100.000"
    )

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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
        ) {
            items(dummyTransactions) { transaction ->
                SiCuanCard(
                    modifier = Modifier.clickable {
                        onNavigateToDetailTransaction(transaction.first)
                    }
                ) {
                    Text(
                        text = transaction.second,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Klik untuk melihat detail",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
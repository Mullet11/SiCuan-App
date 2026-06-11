package com.example.sicuan.presentation.screen.dashboard

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
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit,
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
                text = "Rp0",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Pemasukan: Rp0"
            )

            Text(
                text = "Pengeluaran: Rp0"
            )
        }

        SiCuanPrimaryButton(
            text = "Lihat Transaksi",
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
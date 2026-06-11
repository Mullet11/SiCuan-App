package com.example.sicuan.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SiCuanCard {
            Text(text = "SiCuan")
            Text(text = "Aplikasi manajemen keuangan mahasiswa.")
        }
    }
}
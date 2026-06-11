package com.example.sicuan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTextField
import com.example.sicuan.ui.theme.SiCuanDimens
import com.example.sicuan.ui.theme.SiCuanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SiCuanTheme {
                val transactionName = remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SiCuanDimens.SpacingLg),
                    verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
                ) {
                    Text(
                        text = "SiCuan",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Catat cuan, atur masa depan.",
                        style = MaterialTheme.typography.bodyLarge,
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
                    }

                    SiCuanTextField(
                        value = transactionName.value,
                        onValueChange = { transactionName.value = it },
                        label = "Nama Transaksi",
                        placeholder = "Contoh: Makan siang"
                    )

                    SiCuanPrimaryButton(
                        text = "Simpan Transaksi",
                        onClick = {}
                    )

                    SiCuanSecondaryButton(
                        text = "Scan Struk",
                        onClick = {}
                    )
                }
            }
        }
    }
}
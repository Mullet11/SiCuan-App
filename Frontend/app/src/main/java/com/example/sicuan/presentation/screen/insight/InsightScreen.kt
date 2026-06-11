package com.example.sicuan.presentation.screen.insight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.sicuan.domain.model.CurrencyRate
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanEmptyState
import com.example.sicuan.presentation.component.SiCuanErrorState
import com.example.sicuan.presentation.component.SiCuanLoadingState
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTextField
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InsightScreen(
    currencyRates: List<CurrencyRate>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    val amountText = rememberSaveable {
        mutableStateOf("")
    }

    val selectedQuote = rememberSaveable {
        mutableStateOf("IDR")
    }

    val selectedRate = currencyRates.find { rate ->
        rate.quote == selectedQuote.value
    } ?: currencyRates.firstOrNull()

    val amountValue = amountText.value.toDoubleOrNull()

    val convertedAmount = if (
        amountValue != null &&
        amountValue > 0.0 &&
        selectedRate != null
    ) {
        amountValue * selectedRate.rate
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Insight Keuangan",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SiCuanCard {
            Text(
                text = "Kurs Mata Uang",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Data kurs diambil dari third-party API Frankfurter. Fitur ini digunakan sebagai insight tambahan untuk mahasiswa yang ingin mengetahui nilai tukar mata uang.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        SiCuanPrimaryButton(
            text = "Refresh Data API",
            onClick = onRefresh
        )

        when {
            isLoading -> {
                SiCuanLoadingState(
                    message = "Mengambil data kurs dari API..."
                )
            }

            errorMessage != null -> {
                SiCuanErrorState(
                    title = "Gagal Mengambil Data API",
                    message = errorMessage,
                    actionText = "Coba Lagi",
                    onActionClick = onRefresh
                )
            }

            currencyRates.isEmpty() -> {
                SiCuanEmptyState(
                    title = "Data kurs belum tersedia",
                    message = "Tekan tombol Refresh Data API untuk mengambil data kurs mata uang terbaru."
                )
            }

            else -> {
                CurrencyConverterCard(
                    currencyRates = currencyRates,
                    amountText = amountText.value,
                    selectedQuote = selectedQuote.value,
                    convertedAmount = convertedAmount,
                    selectedRate = selectedRate,
                    onAmountChange = {
                        amountText.value = it
                    },
                    onQuoteSelected = {
                        selectedQuote.value = it
                    }
                )

                Text(
                    text = "Daftar Kurs API",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                currencyRates.forEach { rate ->
                    CurrencyRateItem(rate = rate)
                }
            }
        }

        SiCuanSecondaryButton(
            text = "Kembali",
            onClick = onBack
        )
    }
}

@Composable
private fun CurrencyConverterCard(
    currencyRates: List<CurrencyRate>,
    amountText: String,
    selectedQuote: String,
    convertedAmount: Double?,
    selectedRate: CurrencyRate?,
    onAmountChange: (String) -> Unit,
    onQuoteSelected: (String) -> Unit
) {
    SiCuanCard {
        Text(
            text = "Currency Converter",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Konversi dari USD ke mata uang pilihan berdasarkan data API terbaru.",
            style = MaterialTheme.typography.bodyMedium
        )

        SiCuanTextField(
            value = amountText,
            onValueChange = onAmountChange,
            label = "Nominal USD",
            placeholder = "Contoh: 10",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Text(
            text = "Pilih Mata Uang Tujuan",
            style = MaterialTheme.typography.titleMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm),
            contentPadding = PaddingValues(vertical = SiCuanDimens.SpacingXs)
        ) {
            items(currencyRates) { rate ->
                FilterChip(
                    selected = selectedQuote == rate.quote,
                    onClick = {
                        onQuoteSelected(rate.quote)
                    },
                    label = {
                        Text(text = rate.quote)
                    }
                )
            }
        }

        if (selectedRate != null) {
            Text(
                text = "Kurs: 1 ${selectedRate.base} = ${formatRate(selectedRate.rate)} ${selectedRate.quote}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (convertedAmount != null && selectedRate != null) {
            Text(
                text = "$amountText USD = ${formatRate(convertedAmount)} ${selectedRate.quote}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "Masukkan nominal USD untuk melihat hasil konversi.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CurrencyRateItem(
    rate: CurrencyRate
) {
    SiCuanCard {
        Text(
            text = "${rate.base} ke ${rate.quote}",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "1 ${rate.base} = ${formatRate(rate.rate)} ${rate.quote}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Tanggal data: ${rate.date}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatRate(rate: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    formatter.maximumFractionDigits = 4
    formatter.minimumFractionDigits = 0
    return formatter.format(rate)
}
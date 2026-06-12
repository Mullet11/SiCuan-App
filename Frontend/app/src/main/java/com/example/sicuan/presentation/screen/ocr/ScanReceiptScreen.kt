package com.example.sicuan.presentation.screen.ocr

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.sicuan.data.ocr.ReceiptOcrParser
import com.example.sicuan.domain.model.TransactionCategory
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanLoadingState
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTextField
import com.example.sicuan.presentation.component.TransactionCategorySelector
import com.example.sicuan.ui.theme.SiCuanDimens
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@Composable
fun ScanReceiptScreen(
    transactionErrorMessage: String?,
    onClearMessage: () -> Unit,
    onSaveTransaction: (
        title: String,
        amount: String,
        type: String,
        category: String,
        note: String,
        merchant: String?,
        dateMillis: Long
    ) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val selectedImageUri = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val isScanning = rememberSaveable {
        mutableStateOf(false)
    }

    val localErrorMessage = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val rawText = rememberSaveable {
        mutableStateOf("")
    }

    val title = rememberSaveable {
        mutableStateOf("")
    }

    val amount = rememberSaveable {
        mutableStateOf("")
    }

    val merchant = rememberSaveable {
        mutableStateOf("")
    }

    val dateText = rememberSaveable {
        mutableStateOf("")
    }

    val dateMillis = rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    val note = rememberSaveable {
        mutableStateOf("")
    }

    val category = rememberSaveable {
        mutableStateOf(TransactionCategory.getDefaultCategoryByType(TransactionType.EXPENSE))
    }

    val titleError = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val amountError = rememberSaveable {
        mutableStateOf<String?>(null)
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
                amountError.value = "Nominal hasil OCR tidak boleh kosong"
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

    fun resetOcrResult() {
        rawText.value = ""
        title.value = ""
        amount.value = ""
        merchant.value = ""
        dateText.value = ""
        dateMillis.value = null
        note.value = ""
        titleError.value = null
        amountError.value = null
        localErrorMessage.value = null
        onClearMessage()
    }

    fun processImage(uri: Uri) {
        isScanning.value = true
        localErrorMessage.value = null
        onClearMessage()

        try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text
                    rawText.value = text

                    if (text.isBlank()) {
                        title.value = ""
                        amount.value = ""
                        merchant.value = ""
                        dateText.value = ""
                        dateMillis.value = null
                        note.value = ""
                        localErrorMessage.value =
                            "Teks tidak terbaca. Coba gunakan foto struk yang lebih jelas."
                    } else {
                        val parsedResult = ReceiptOcrParser.parse(text)

                        title.value = parsedResult.title
                        amount.value = parsedResult.amountText
                        merchant.value = parsedResult.merchant
                        dateText.value = parsedResult.dateText
                        dateMillis.value = parsedResult.dateMillis
                        note.value = parsedResult.note

                        if (parsedResult.amountText.isBlank()) {
                            localErrorMessage.value =
                                "OCR berhasil membaca teks, tetapi nominal belum ditemukan. Silakan isi nominal secara manual."
                        }
                    }

                    isScanning.value = false
                }
                .addOnFailureListener { error ->
                    isScanning.value = false
                    localErrorMessage.value =
                        error.message ?: "Gagal membaca teks dari gambar"
                }
        } catch (error: Exception) {
            isScanning.value = false
            localErrorMessage.value =
                error.message ?: "Gagal membuka gambar"
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri.value = uri.toString()
            resetOcrResult()
            processImage(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SiCuanDimens.SpacingLg),
        verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingMd)
    ) {
        Text(
            text = "Scan Struk OCR",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        SiCuanCard {
            Text(
                text = "OCR Receipt Scanner",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Pilih foto struk dari galeri. Aplikasi akan membaca teks menggunakan ML Kit, lalu mencoba mengambil merchant, nominal, dan tanggal transaksi.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (selectedImageUri.value != null) {
                Text(
                    text = "Gambar struk sudah dipilih.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        SiCuanPrimaryButton(
            text = "Pilih Foto Struk",
            onClick = {
                imagePickerLauncher.launch("image/*")
            }
        )

        if (isScanning.value) {
            SiCuanLoadingState(
                message = "Membaca teks dari struk..."
            )
        }

        if (!localErrorMessage.value.isNullOrBlank()) {
            Text(
                text = localErrorMessage.value.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!transactionErrorMessage.isNullOrBlank()) {
            Text(
                text = transactionErrorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (rawText.value.isNotBlank()) {
            SiCuanCard {
                Text(
                    text = "Preview Hasil OCR",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Periksa kembali data hasil scan. Kamu bisa mengedit data sebelum disimpan sebagai transaksi.",
                    style = MaterialTheme.typography.bodyMedium
                )

                SiCuanTextField(
                    value = title.value,
                    onValueChange = {
                        title.value = it
                        titleError.value = null
                        onClearMessage()
                    },
                    label = "Nama Transaksi",
                    placeholder = "Contoh: Belanja di Indomaret",
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
                    placeholder = "Contoh: 25000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError.value != null,
                    supportingText = amountError.value
                )

                SiCuanTextField(
                    value = merchant.value,
                    onValueChange = {
                        merchant.value = it
                        onClearMessage()
                    },
                    label = "Merchant",
                    placeholder = "Contoh: Indomaret"
                )

                SiCuanTextField(
                    value = dateText.value,
                    onValueChange = {
                        dateText.value = it
                        onClearMessage()
                    },
                    label = "Tanggal Struk",
                    placeholder = "Contoh: 12/06/2026"
                )

                Text(
                    text = "Jenis Transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = {
                            Text(text = "Pengeluaran")
                        }
                    )
                }

                TransactionCategorySelector(
                    type = TransactionType.EXPENSE,
                    selectedCategory = category.value,
                    onCategorySelected = {
                        category.value = it
                        onClearMessage()
                    }
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
                    text = "Simpan sebagai Transaksi",
                    onClick = {
                        if (validateForm()) {
                            onSaveTransaction(
                                title.value,
                                amount.value,
                                TransactionType.EXPENSE,
                                category.value,
                                note.value,
                                merchant.value.ifBlank { null },
                                dateMillis.value ?: System.currentTimeMillis()
                            )
                        }
                    }
                )
            }

            SiCuanCard {
                Text(
                    text = "Raw Text OCR",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = rawText.value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        SiCuanSecondaryButton(
            text = "Kembali",
            onClick = onBack
        )
    }
}
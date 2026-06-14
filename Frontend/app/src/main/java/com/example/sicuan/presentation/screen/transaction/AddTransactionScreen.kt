package com.example.sicuan.presentation.screen.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicuan.domain.model.TransactionCategory
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.ui.theme.SiCuanDimens
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    errorMessage: String?,
    prefilledAmount: String?,
    prefilledTitle: String? = null,
    prefilledNote: String? = null,
    prefilledMerchant: String? = null,
    prefilledCategory: String? = null,
    prefilledDateMillis: Long? = null,
    onClearMessage: () -> Unit,
    onBack: () -> Unit,
    onSaveTransaction: (
        title: String,
        amount: String,
        type: String,
        category: String,
        note: String,
        merchant: String?,
        dateMillis: Long
    ) -> Unit
) {
    var rawAmount by rememberSaveable(prefilledAmount) {
        mutableStateOf(prefilledAmount?.replace(Regex("[^\\d]"), "") ?: "")
    }
    var type by rememberSaveable { mutableStateOf(TransactionType.EXPENSE) }

    var customCategories by rememberSaveable { mutableStateOf(listOf<String>()) }
    val defaultCategories = TransactionCategory.getCategoriesByType(type)
    val allCategories = defaultCategories + customCategories
    var category by rememberSaveable(type, prefilledCategory) {
        mutableStateOf(prefilledCategory ?: allCategories.firstOrNull() ?: "Umum")
    }

    var note by rememberSaveable(prefilledNote) { mutableStateOf(prefilledNote ?: "") }
    var merchantName by rememberSaveable(prefilledMerchant) { mutableStateOf(prefilledMerchant ?: "") }

    var confirmedDateMillis by rememberSaveable(prefilledDateMillis) { mutableStateOf(prefilledDateMillis ?: System.currentTimeMillis()) }
    var tempDateMillis by remember { mutableStateOf(confirmedDateMillis) }

    var showCustomCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showDateTimeConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = tempDateMillis

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.timeInMillis = tempDateMillis
                newCalendar.set(year, month, dayOfMonth)
                tempDateMillis = newCalendar.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showTimePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = tempDateMillis

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCalendar = Calendar.getInstance()
                newCalendar.timeInMillis = tempDateMillis
                newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                newCalendar.set(Calendar.MINUTE, minute)
                newCalendar.set(Calendar.SECOND, 0)
                tempDateMillis = newCalendar.timeInMillis
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SiCuanDimens.SpacingLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onBack() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(SiCuanDimens.SpacingMd))
            Text(
                text = "Tambah Transaksi",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SiCuanDimens.SpacingLg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(SiCuanDimens.CardRadius))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SegmentedButtonItem(
                    text = "Pengeluaran",
                    isSelected = type == TransactionType.EXPENSE,
                    onClick = { type = TransactionType.EXPENSE; onClearMessage() },
                    modifier = Modifier.weight(1f)
                )
                SegmentedButtonItem(
                    text = "Pemasukan",
                    isSelected = type == TransactionType.INCOME,
                    onClick = { type = TransactionType.INCOME; onClearMessage() },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingXl))

            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = SiCuanDimens.SpacingMd),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = rawAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            rawAmount = it
                            onClearMessage()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CurrencyAmountInputVisualTransformation(),
                    textStyle = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Rp ",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (rawAmount.isEmpty()) {
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            } else {
                                innerTextField()
                            }
                        }
                    }
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))

                OutlinedTextField(
                value = merchantName,
                onValueChange = { merchantName = it; onClearMessage() },
                placeholder = { Text("Nama Merchant (Opsional)") },
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SiCuanDimens.InputRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it; onClearMessage() },
                placeholder = { Text("Keterangan (Opsional)") },
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SiCuanDimens.InputRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))

            Text(
                text = "WAKTU TRANSAKSI",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingSm))

            val sdfConfirmed = SimpleDateFormat("EEEE, dd MMM yyyy - HH:mm:ss", Locale("id", "ID"))
            Text(
                text = sdfConfirmed.format(Date(confirmedDateMillis)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm)) {
                DateChip("Hari ini") {
                    tempDateMillis = System.currentTimeMillis()
                    showDateTimeConfirmDialog = true
                }
                DateChip("Kemarin") {
                    tempDateMillis = System.currentTimeMillis() - 86400000L
                    showDateTimeConfirmDialog = true
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(SiCuanDimens.CardRadius))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable {
                            tempDateMillis = confirmedDateMillis
                            showDateTimeConfirmDialog = true
                        }
                        .padding(horizontal = SiCuanDimens.SpacingMd, vertical = SiCuanDimens.SpacingSm),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Kalender",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))

            Text(
                text = "KATEGORI",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingSm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allCategories.forEach { cat ->
                    CategoryChip(
                        text = cat,
                        isSelected = category == cat,
                        onClick = { category = cat; onClearMessage() }
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(SiCuanDimens.CardRadius))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { showCustomCategoryDialog = true }
                        .padding(horizontal = SiCuanDimens.SpacingMd, vertical = SiCuanDimens.SpacingSm),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Kategori",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SiCuanDimens.SpacingXl))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(SiCuanDimens.SpacingLg)
        ) {
            Button(
                onClick = {
                    val finalTitle = prefilledTitle ?: if (note.isBlank()) category else category
                    onSaveTransaction(
                        finalTitle,
                        rawAmount,
                        type,
                        category,
                        note,
                        merchantName.ifBlank { null },
                        confirmedDateMillis
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(SiCuanDimens.ButtonRadius)
            ) {
                Text("Simpan Transaksi", style = MaterialTheme.typography.titleMedium)
            }
        }
    }


    if (showCustomCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCustomCategoryDialog = false },
            title = { Text("Kategori Baru") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("Nama kategori") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        customCategories = customCategories + newCategoryName.trim()
                        category = newCategoryName.trim()
                    }
                    newCategoryName = ""
                    showCustomCategoryDialog = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomCategoryDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDateTimeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDateTimeConfirmDialog = false },
            title = { Text("Konfirmasi Waktu") },
            text = {
                Column {
                    Text(
                        text = "Waktu yang akan dicatat:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val sdfDate = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
                    val sdfTime = SimpleDateFormat("HH:mm:ss", Locale("id", "ID"))

                    Text(
                        text = sdfDate.format(Date(tempDateMillis)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Jam: ${sdfTime.format(Date(tempDateMillis))}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = { showDatePicker() }) {
                            Text("Ubah Tanggal")
                        }
                        OutlinedButton(onClick = { showTimePicker() }) {
                            Text("Ubah Jam")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    confirmedDateMillis = tempDateMillis
                    showDateTimeConfirmDialog = false
                }) {
                    Text("Konfirmasi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateTimeConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SegmentedButtonItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(SiCuanDimens.CardRadius))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = SiCuanDimens.SpacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun DateChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(SiCuanDimens.CardRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(horizontal = SiCuanDimens.SpacingMd, vertical = SiCuanDimens.SpacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(SiCuanDimens.CardRadius))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(horizontal = SiCuanDimens.SpacingMd, vertical = SiCuanDimens.SpacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

class CurrencyAmountInputVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedText = try {
            val parsed = originalText.toLong()
            java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(parsed)
        } catch (e: Exception) {
            originalText
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var commas = 0
                var i = 0
                var originalPos = 0
                while (originalPos < offset && i < formattedText.length) {
                    if (formattedText[i] == '.') {
                        commas++
                    } else {
                        originalPos++
                    }
                    i++
                }
                return offset + commas
            }

            override fun transformedToOriginal(offset: Int): Int {
                var commas = 0
                for (i in 0 until java.lang.Math.min(offset, formattedText.length)) {
                    if (formattedText[i] == '.') commas++
                }
                return offset - commas
            }
        }
        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}

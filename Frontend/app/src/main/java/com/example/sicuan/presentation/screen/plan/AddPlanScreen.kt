package com.example.sicuan.presentation.screen.plan

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlanScreen(
    onBack: () -> Unit,
    onSave: (title: String, targetAmountText: String, deadlineMillis: Long) -> Unit,
    errorMessage: String? = null,
    onClearMessage: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var deadlineMillis by remember { mutableStateOf(0L) }
    var deadlineText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            deadlineMillis = selectedCalendar.timeInMillis
            
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            deadlineText = dateFormat.format(selectedCalendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Colors to match dark mode screenshot
    val darkBackground = MaterialTheme.colorScheme.background
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val inputBackground = MaterialTheme.colorScheme.surface
    val primaryText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val buttonColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // Can show snackbar if needed
            // onClearMessage()
        }
    }

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Tambah Target", color = primaryText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackground
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Button(
                    onClick = { onSave(title, targetAmount, deadlineMillis) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan Target", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.CheckCircleOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "Langkah pertama untuk mewujudkan mimpimu dimulai dari sini.",
                color = secondaryText,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
            
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Nama Target
                    Column {
                        Text("Nama Target", color = secondaryText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Misal: Rumah Impian", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = inputBackground,
                                unfocusedContainerColor = inputBackground,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = primaryText,
                                unfocusedTextColor = primaryText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Target Nominal
                    Column {
                        Text("Target Nominal", color = secondaryText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = targetAmount,
                            onValueChange = { newValue ->
                                val cleanString = newValue.replace("[^\\d]".toRegex(), "")
                                if (cleanString.isNotEmpty()) {
                                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                                    val localeID = Locale("in", "ID")
                                    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                                    formatRupiah.maximumFractionDigits = 0
                                    targetAmount = formatRupiah.format(parsed).replace("Rp", "").trim()
                                } else {
                                    targetAmount = ""
                                }
                            },
                            leadingIcon = { Text("Rp", color = primaryText, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                            placeholder = { Text("0", color = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = inputBackground,
                                unfocusedContainerColor = inputBackground,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = primaryText,
                                unfocusedTextColor = primaryText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Target Tercapai (Date)
                    Column {
                        Text("Target Tercapai", color = secondaryText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deadlineText,
                            onValueChange = { },
                            readOnly = true,
                            enabled = false,
                            placeholder = { Text("dd/mm/yyyy", color = Color.Gray) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date",
                                    tint = secondaryText
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = inputBackground,
                                disabledBorderColor = Color.Transparent,
                                disabledTextColor = primaryText,
                                disabledPlaceholderColor = Color.Gray,
                                disabledTrailingIconColor = secondaryText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}

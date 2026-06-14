package com.example.sicuan.presentation.screen.plan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sicuan.domain.model.Plan
import com.example.sicuan.domain.model.PlanHistory
import com.example.sicuan.domain.model.PlanHistoryType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    plan: Plan?,
    histories: List<PlanHistory>,
    errorMessage: String?,
    successMessage: String?,
    onBack: () -> Unit,
    onTopUp: (String) -> Unit,
    onWithdraw: (String) -> Unit,
    onDelete: () -> Unit,
    onClearMessage: () -> Unit
) {
    val darkBackground = MaterialTheme.colorScheme.background
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val primaryText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val buttonColor = MaterialTheme.colorScheme.primary
    val progressColor = MaterialTheme.colorScheme.primary

    var showTopUpSheet by remember { mutableStateOf(false) }
    var showWithdrawSheet by remember { mutableStateOf(false) }
    var recommendedTopUp by remember { mutableStateOf(0.0) }

    var monthsRemaining by remember { mutableStateOf(1) }

    LaunchedEffect(plan) {
        if (plan != null) {
            val now = System.currentTimeMillis()
            val diff = plan.deadlineDateMillis - now
            monthsRemaining = if (diff > 0) {
                ceil(diff.toDouble() / (1000.0 * 60 * 60 * 24 * 30)).toInt().coerceAtLeast(1)
            } else {
                1
            }
            val remainingAmount = plan.targetAmount - plan.savedAmount
            recommendedTopUp = if (remainingAmount > 0) remainingAmount / monthsRemaining else 0.0
        }
    }

    LaunchedEffect(successMessage, errorMessage) {
        if (successMessage != null || errorMessage != null) {
        }
    }

    if (plan == null) {
        Box(modifier = Modifier.fillMaxSize().background(darkBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = buttonColor)
        }
        return
    }

    val isTargetReached = plan.savedAmount >= plan.targetAmount

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text(plan.title, color = primaryText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryText)
                    }
                },
                actions = {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackground
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (plan.savedAmount > 0) {
                    OutlinedButton(
                        onClick = { showWithdrawSheet = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tarik Dana", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = { showTopUpSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isTargetReached
                ) {
                    if (isTargetReached) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Target Tercapai!", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Nabung Sekarang", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    var animationPlayed by remember { mutableStateOf(false) }
                    val currentProgress by animateFloatAsState(
                        targetValue = if (animationPlayed) plan.progressPercentage else 0f,
                        animationSpec = tween(durationMillis = 1500, delayMillis = 200),
                        label = "circular_progress"
                    )

                    LaunchedEffect(key1 = true) {
                        animationPlayed = true
                    }

                    CircularProgressIndicator(
                        progress = { currentProgress },
                        modifier = Modifier.size(240.dp),
                        color = progressColor,
                        strokeWidth = 16.dp,
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                        strokeCap = StrokeCap.Round
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(currentProgress * 100).toInt()}%",
                            color = primaryText,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatRupiah(plan.savedAmount),
                            color = primaryText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "dari ${formatRupiah(plan.targetAmount)}",
                            color = secondaryText,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                if (!isTargetReached && recommendedTopUp > 0) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tips Capai Target",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Untuk mencapai target tepat waktu ($monthsRemaining bulan lagi), kamu disarankan untuk menabung sebesar:",
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${formatRupiah(recommendedTopUp)} / bulan",
                                color = progressColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                } else if (isTargetReached) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🎉 Selamat! Target Tercapai!",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Riwayat Menabung",
                    color = primaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (histories.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada riwayat tabungan. Ayo mulai menabung!",
                        color = secondaryText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            } else {
                items(histories) { history ->
                    HistoryItem(history)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showTopUpSheet) {
        TopUpBottomSheet(
            recommendedAmount = recommendedTopUp,
            onDismiss = { showTopUpSheet = false },
            onConfirm = { amountText ->
                onTopUp(amountText)
                showTopUpSheet = false
            }
        )
    }

    if (showWithdrawSheet) {
        WithdrawBottomSheet(
            maxAmount = plan.savedAmount,
            onDismiss = { showWithdrawSheet = false },
            onConfirm = { amountText ->
                onWithdraw(amountText)
                showWithdrawSheet = false
            }
        )
    }
}

@Composable
fun HistoryItem(history: PlanHistory) {
    val isTopUp = history.type == PlanHistoryType.TOP_UP
    val amountPrefix = if (isTopUp) "+" else "-"
    val amountColor = if (isTopUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val icon = if (isTopUp) Icons.Default.Add else Icons.Default.Remove

    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val dateString = dateFormat.format(Date(history.dateMillis))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = amountColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isTopUp) "Nabung" else "Tarik Dana",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = dateString,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
        Text(
            text = "$amountPrefix ${formatRupiah(history.amount)}",
            color = amountColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpBottomSheet(
    recommendedAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val darkBackground = MaterialTheme.colorScheme.background
    val inputBackground = MaterialTheme.colorScheme.surfaceVariant
    val primaryText = MaterialTheme.colorScheme.onBackground
    val buttonColor = MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tambah Tabungan",
                color = primaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { newValue ->
                    val cleanString = newValue.replace("[^0-9]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDoubleOrNull() ?: 0.0
                        val localeID = Locale("in", "ID")
                        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                        formatRupiah.maximumFractionDigits = 0
                        amountText = formatRupiah.format(parsed).replace("Rp", "").trim()
                    } else {
                        amountText = ""
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

            if (recommendedAmount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    AssistChip(
                        onClick = {
                            val localeID = Locale("in", "ID")
                            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                            formatRupiah.maximumFractionDigits = 0
                            amountText = formatRupiah.format(recommendedAmount).replace("Rp", "").trim()
                        },
                        label = { Text("Isi Sesuai Saran", color = primaryText) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = darkBackground)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onConfirm(amountText) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    formatRupiah.maximumFractionDigits = 0
    return formatRupiah.format(amount).replace("Rp", "Rp ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawBottomSheet(
    maxAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val primaryText = MaterialTheme.colorScheme.onBackground
    val inputBackground = MaterialTheme.colorScheme.surfaceVariant
    val secondaryText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val buttonColor = MaterialTheme.colorScheme.error

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tarik Dana Tabungan",
                color = primaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Saldo tersedia: ${formatRupiah(maxAmount)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { newValue ->
                    val cleanString = newValue.replace("[^0-9]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDoubleOrNull() ?: 0.0
                        val localeID = Locale("in", "ID")
                        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                        formatRupiah.maximumFractionDigits = 0
                        amountText = formatRupiah.format(parsed).replace("Rp", "").trim()
                    } else {
                        amountText = ""
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

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onConfirm(amountText) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tarik", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

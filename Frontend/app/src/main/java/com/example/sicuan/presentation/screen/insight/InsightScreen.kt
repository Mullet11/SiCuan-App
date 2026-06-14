package com.example.sicuan.presentation.screen.insight

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sicuan.R
import com.example.sicuan.domain.model.CurrencyRate
import com.example.sicuan.domain.model.Transaction
import com.example.sicuan.domain.model.TransactionType
import com.example.sicuan.presentation.viewmodel.ReportData
import com.example.sicuan.util.PdfExportHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightScreen(
    userName: String?,
    photoUrl: String?,
    transactions: List<Transaction>,
    currencyRates: List<CurrencyRate>,
    exportedReports: List<ReportData>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onDeleteReport: (Long) -> Unit,
    onBack: () -> Unit
) {
    val darkBackground = MaterialTheme.colorScheme.background
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val primaryText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val progressColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primaryContainer

    // Calculate Score — konsisten dengan Dashboard
    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val expenseRatio = if (totalIncome > 0) (totalExpense / totalIncome) else 0.0
    val score = when {
        totalIncome == 0.0 && totalExpense == 0.0 -> 0
        else -> ((1.0 - expenseRatio).coerceIn(0.0, 1.0) * 100).toInt()
    }

    val scoreTitle = when {
        totalIncome == 0.0 && totalExpense == 0.0 -> "Mulai Catat"
        expenseRatio <= 0.4 -> "Sangat Baik"
        expenseRatio <= 0.6 -> "Baik"
        expenseRatio <= 0.8 -> "Cukup Baik"
        expenseRatio <= 1.0 -> "Perlu Perhatian"
        else -> "Buruk"
    }
    
    val scoreDesc = when {
        totalIncome == 0.0 && totalExpense == 0.0 -> "Belum ada data transaksi. Mulai catat pemasukan dan pengeluaranmu!"
        expenseRatio <= 0.4 -> "Luar biasa! Pengeluaranmu sangat terkendali. Kamu menabung lebih dari 60% penghasilan!"
        expenseRatio <= 0.6 -> "Keuanganmu baik dan stabil. Kamu menabung 40-60% dari penghasilanmu. Pertahankan!"
        expenseRatio <= 0.8 -> "Cukup baik, tapi masih ada ruang untuk lebih hemat. Coba kurangi pengeluaran yang tidak perlu."
        expenseRatio <= 1.0 -> "Pengeluaranmu mendekati batas pemasukan. Evaluasi kembali dan prioritaskan kebutuhan."
        else -> "Pengeluaranmu melebihi pemasukan! Segera evaluasi dan kurangi pengeluaran yang tidak penting."
    }

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Halo,",
                            color = secondaryText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = userName?.split(" ")?.firstOrNull() ?: "Pengguna!",
                            color = primaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                        AsyncImage(
                            model = photoUrl ?: R.drawable.ic_launcher_background, // Placeholder
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
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
                Spacer(modifier = Modifier.height(8.dp))
                // Financial Score Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var animationPlayed by remember { mutableStateOf(false) }
                        val currentProgress by animateFloatAsState(
                            targetValue = if (animationPlayed) score / 100f else 0f,
                            animationSpec = tween(durationMillis = 1500, delayMillis = 200),
                            label = "score_progress"
                        )

                        LaunchedEffect(key1 = true) {
                            animationPlayed = true
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(160.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { currentProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = progressColor,
                                strokeWidth = 16.dp,
                                trackColor = trackColor,
                                strokeCap = StrokeCap.Round
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${(currentProgress * 100).toInt()}",
                                    color = primaryText,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "/100",
                                    color = secondaryText,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = scoreTitle,
                            color = primaryText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = scoreDesc,
                            color = secondaryText,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BadgeItem(text = when {
                                expenseRatio <= 0.4 -> "Subjektif: Sangat Baik"
                                expenseRatio <= 0.6 -> "Subjektif: Baik"
                                expenseRatio <= 0.8 -> "Subjektif: Cukup"
                                else -> "Subjektif: Kurang"
                            }, modifier = Modifier.weight(1f))
                            BadgeItem(text = when {
                                expenseRatio <= 0.6 -> "Objektif: Seimbang"
                                expenseRatio <= 1.0 -> "Objektif: Perlu Kalibrasi"
                                else -> "Objektif: Defisit"
                            }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edukasi Untukmu",
                        color = primaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lihat Semua",
                        color = secondaryText,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateToEducation() }
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val uriHandler = LocalUriHandler.current
                    EducationCard(
                        category = "Budgeting",
                        title = "Budgeting Mahir untuk Mahasiswa",
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Article/20546") }
                    )
                    EducationCard(
                        category = "Investasi",
                        title = "Investasi 101: Mulai dari Nol",
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Category/131") }
                    )
                }
            }

            item {
                Text(
                    text = "Laporan & Ekspor",
                    color = primaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (exportedReports.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.Description,
                                contentDescription = null,
                                tint = secondaryText,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum ada laporan",
                                color = primaryText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ekspor laporan dari halaman Profil untuk melihat di sini.",
                                color = secondaryText,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(exportedReports, key = { it.id }) { report ->
                    val context = LocalContext.current
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = report.title,
                                        color = primaryText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = report.subtitle,
                                        color = secondaryText,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Financial summary mini row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MiniStat(label = "Pemasukan", value = formatRupiah(report.totalIncome), color = MaterialTheme.colorScheme.primary)
                                MiniStat(label = "Pengeluaran", value = formatRupiah(report.totalExpense), color = MaterialTheme.colorScheme.error)
                                MiniStat(label = "Saldo", value = formatRupiah(report.balance), color = MaterialTheme.colorScheme.secondary)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(4.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Share
                                IconButton(onClick = {
                                    val shareText = buildString {
                                        appendLine("\uD83D\uDCCA *${report.title}*")
                                        appendLine(report.subtitle)
                                        appendLine("")
                                        appendLine("\u2705 Pemasukan: ${formatRupiah(report.totalIncome)}")
                                        appendLine("\u274C Pengeluaran: ${formatRupiah(report.totalExpense)}")
                                        appendLine("\uD83D\uDCB0 Saldo: ${formatRupiah(report.balance)}")
                                        appendLine("")
                                        appendLine("Dikirim via SiCuan App")
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Bagikan Laporan"))
                                }) {
                                    Icon(Icons.Outlined.Share, contentDescription = "Bagikan", tint = MaterialTheme.colorScheme.secondary)
                                }
                                // Download
                                IconButton(onClick = {
                                    PdfExportHelper.generateReportPdf(context, report)
                                }) {
                                    Icon(Icons.Outlined.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary)
                                }
                                // Delete
                                IconButton(onClick = {
                                    onDeleteReport(report.id)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Nav bar space
            }
        }
    }
}

@Composable
fun BadgeItem(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EducationCard(category: String, title: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://img.youtube.com/vi/qQ1OaI4Z92g/0.jpg",
                        contentDescription = "Thumbnail Video",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Durasi: 5 Menit",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun RowScope.MiniStat(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun formatRupiah(amount: Double): String {
    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
    return formatter.format(amount)
}

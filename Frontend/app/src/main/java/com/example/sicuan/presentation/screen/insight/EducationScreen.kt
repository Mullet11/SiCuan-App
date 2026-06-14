package com.example.sicuan.presentation.screen.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(
    userName: String,
    photoUrl: String?,
    dob: String?,
    onNavigateToQuiz: () -> Unit,
    onBack: () -> Unit
) {
    val darkBackground = MaterialTheme.colorScheme.background
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val primaryText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    val uriHandler = LocalUriHandler.current

    var showGlossary by remember { mutableStateOf(false) }

    fun calculateAge(dobStr: String?): Int {
        if (dobStr.isNullOrEmpty()) return 0
        return try {
            val parts = dobStr.split("/")
            if (parts.size == 3) {
                val year = parts[2].toInt()
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                currentYear - year
            } else 0
        } catch (e: Exception) { 0 }
    }

    val userAge = remember(dob) { calculateAge(dob) }

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
                            text = userName.split(" ").firstOrNull() ?: "Pengguna",
                            color = primaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                        if (photoUrl != null) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile Picture",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Uji Literasi Keuanganmu",
                                color = primaryText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sempurnakan skor kalibrasimu untuk mendapatkan rekomendasi materi yang lebih personal dan akurat.",
                            color = secondaryText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNavigateToQuiz,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.PlayCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mulai Kuis Kalibrasi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
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
                        text = if (userAge in 18..22) "Edukasi Mahasiswa" else if (userAge in 23..30) "Edukasi Pekerja Awal" else if (userAge > 30) "Edukasi Lanjutan" else "Edukasi",
                        color = primaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showGlossary = true }) {
                        Icon(Icons.Outlined.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Glosarium", fontSize = 14.sp)
                    }
                }
            }

            if (userAge in 18..22) {
                item {
                    EducationCardLarge(
                        category = "BUDGETING",
                        title = "Budgeting Mahir untuk Mahasiswa",
                        duration = "5 Min Read • Tingkat Dasar",
                        icon = Icons.Outlined.AccountBalanceWallet,
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Article/20546") }
                    )
                }
                item {
                    EducationCardLarge(
                        category = "DANA DARURAT",
                        title = "Siapkan Dana Darurat Mahasiswa",
                        duration = "4 Min Read • Tingkat Dasar",
                        icon = Icons.Outlined.Shield,
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Article/10444") }
                    )
                }
            } else if (userAge in 23..30) {
                item {
                    EducationCardLarge(
                        category = "INVESTASI",
                        title = "Investasi Saham untuk Pemula",
                        duration = "8 Min Read • Menengah",
                        icon = Icons.Outlined.TrendingUp,
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Category/131") }
                    )
                }
                item {
                    EducationCardLarge(
                        category = "KREDIT",
                        title = "Mengelola Cicilan Pertama Anda",
                        duration = "6 Min Read • Menengah",
                        icon = Icons.Outlined.AccountBalance,
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Article/10444") }
                    )
                }
            } else if (userAge > 30) {
                item {
                    EducationCardLarge(
                        category = "PENSIUN",
                        title = "Merencanakan Dana Pensiun Mapan",
                        duration = "10 Min Read • Lanjutan",
                        icon = Icons.Outlined.TrendingUp,
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Category/131") }
                    )
                }
                item {
                    EducationCardLarge(
                        category = "ASURANSI",
                        title = "Melindungi Keluarga dengan Asuransi",
                        duration = "7 Min Read • Lanjutan",
                        icon = Icons.Outlined.Shield,
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Article/10444") }
                    )
                }
            } else {
                item {
                    EducationCardLarge(
                        category = "DASAR KEUANGAN",
                        title = "Panduan Memulai Literasi Keuangan",
                        duration = "5 Min Read • Umum",
                        icon = Icons.Outlined.AccountBalanceWallet,
                        onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/") }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showGlossary) {
        AlertDialog(
            onDismissRequest = { showGlossary = false },
            title = { Text("Glosarium Keuangan") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Text("• Inflasi: Kenaikan harga barang/jasa secara umum yang menurunkan daya beli uang dari waktu ke waktu.", fontWeight = FontWeight.Bold)
                    }
                    item {
                        Text("• Diversifikasi: Strategi menyebar investasi ke berbagai aset untuk mengurangi risiko.", fontWeight = FontWeight.Bold)
                    }
                    item {
                        Text("• Suku Bunga: Biaya meminjam uang (kredit) atau hasil dari menabung/investasi.", fontWeight = FontWeight.Bold)
                    }
                    item {
                        Text("• Dana Darurat: Simpanan khusus yang mudah dicairkan untuk kebutuhan mendesak yang tak terduga.", fontWeight = FontWeight.Bold)
                    }
                    item {
                        Text("• Dunning-Kruger Effect: Bias kognitif di mana seseorang merasa jauh lebih paham tentang keuangan dibanding kenyataan sebenarnya.", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGlossary = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun EducationCardLarge(
    category: String,
    title: String,
    duration: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = category,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_recent_history),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = duration,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

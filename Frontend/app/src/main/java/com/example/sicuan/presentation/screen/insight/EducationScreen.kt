package com.example.sicuan.presentation.screen.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(
    onBack: () -> Unit
) {
    val darkBackground = Color(0xFF0B1914)
    val cardBackground = Color(0xFF1E3A2F)
    val primaryText = Color.White
    val secondaryText = Color(0xFFA0A0A0)
    val progressColor = Color(0xFF4CAF50)

    val uriHandler = LocalUriHandler.current

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Edukasi Finansial", color = primaryText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
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
                // Kalibrasi Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Uji Literasi Keuanganmu",
                            color = primaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sempurnakan skor kalibrasimu untuk mendapatkan rekomendasi materi yang lebih akurat.",
                            color = secondaryText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B7A57)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.PlayCircleOutline, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mulai Kuis Kalibrasi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Edukasi",
                    color = primaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                EducationCardLarge(
                    category = "BUDGETING",
                    title = "Budgeting Mahir untuk Mahasiswa",
                    duration = "5 Min Read • Tingkat Lanjut",
                    onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Article/20546") }
                )
            }

            item {
                EducationCardLarge(
                    category = "INVESTASI",
                    title = "Investasi 101: Memulai dari Nol",
                    duration = "8 Min Read • Pemula",
                    onClick = { uriHandler.openUri("https://sikapiuangmu.ojk.go.id/FrontEnd/CMS/Category/131") }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun EducationCardLarge(
    category: String,
    title: String,
    duration: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A2F)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF0B1914)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for image
                Text("📚 Image Placeholder", color = Color.Gray)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = category,
                    color = Color(0xFF4CAF50),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_recent_history),
                        contentDescription = null,
                        tint = Color(0xFFA0A0A0),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = duration,
                        color = Color(0xFFA0A0A0),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

package com.example.sicuan.presentation.screen.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTopAppBar
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun EducationScreen(
    onBack: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) } // 0: Start, 1: Quiz, 2: Result/Module
    var score by remember { mutableIntStateOf(0) }
    var currentQuestion by remember { mutableIntStateOf(0) }

    val questions = listOf(
        Pair("Apa itu Dana Darurat?", listOf(
            "Uang untuk liburan dadakan",
            "Dana cadangan untuk kebutuhan mendesak yang tidak terduga",
            "Sisa uang bulanan"
        )),
        Pair("Berapa persentase ideal untuk ditabung dari penghasilan menurut aturan 50/30/20?", listOf(
            "50%",
            "30%",
            "20%"
        )),
        Pair("Apa itu Inflasi?", listOf(
            "Penurunan harga barang",
            "Kenaikan harga barang dan jasa secara umum terus menerus",
            "Investasi yang selalu untung"
        ))
    )

    val correctAnswers = listOf(1, 2, 1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SiCuanTopAppBar(userName = "Edukasi Finansial")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SiCuanDimens.SpacingLg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingLg)
        ) {
            when (step) {
                0 -> {
                    // Start Screen
                    SiCuanCard {
                        Text(
                            text = "Kalibrasi Literasi Keuangan",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Mari cari tahu sejauh mana pemahamanmu tentang keuangan. Jawab 3 pertanyaan singkat untuk mendapatkan modul yang sesuai untukmu!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        SiCuanPrimaryButton(text = "Mulai Kuis", onClick = { step = 1 })
                    }
                }
                1 -> {
                    // Quiz
                    val q = questions[currentQuestion]
                    SiCuanCard {
                        Text(
                            text = "Pertanyaan ${currentQuestion + 1} dari ${questions.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingSm))
                        Text(
                            text = q.first,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingMd))
                        
                        q.second.forEachIndexed { index, answer ->
                            SiCuanSecondaryButton(
                                text = answer,
                                onClick = {
                                    if (index == correctAnswers[currentQuestion]) score++
                                    
                                    if (currentQuestion < questions.size - 1) {
                                        currentQuestion++
                                    } else {
                                        step = 2
                                    }
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Result and Modules
                    val level = when (score) {
                        0, 1 -> "Pemula"
                        2 -> "Menengah"
                        else -> "Mahir"
                    }
                    
                    SiCuanCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            text = "Level Literasi: $level",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Skormu $score dari 3. Kami telah menyiapkan modul edukasi yang dipersonalisasi untuk tingkat pengetahuanmu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        text = "Modul Rekomendasi",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Modules based on level
                    if (level == "Pemula") {
                        ModuleItem("Dasar Budgeting 50/30/20", "Pelajari cara membagi gaji dengan benar.")
                        ModuleItem("Pentingnya Mencatat Pengeluaran", "Mulai kebiasaan baik dari hal kecil.")
                    } else if (level == "Menengah") {
                        ModuleItem("Membangun Dana Darurat", "Langkah aman bebas dari utang mendadak.")
                        ModuleItem("Mengenal Instrumen Investasi", "Deposito, Reksadana, dan Saham.")
                    } else {
                        ModuleItem("Diversifikasi Portofolio", "Jangan taruh semua telur di satu keranjang.")
                        ModuleItem("Melawan Inflasi Jangka Panjang", "Strategi investasi advanced.")
                    }
                    
                    Spacer(modifier = Modifier.height(SiCuanDimens.SpacingLg))
                    SiCuanSecondaryButton(text = "Ulangi Kuis", onClick = {
                        step = 0
                        score = 0
                        currentQuestion = 0
                    })
                }
            }

            SiCuanSecondaryButton(text = "Kembali ke Dashboard", onClick = onBack)
        }
    }
}

@Composable
private fun ModuleItem(title: String, desc: String) {
    SiCuanCard {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = desc, style = MaterialTheme.typography.bodyMedium)
    }
}

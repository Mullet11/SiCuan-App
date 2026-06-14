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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import com.example.sicuan.presentation.component.SiCuanCard
import com.example.sicuan.presentation.component.SiCuanPrimaryButton
import com.example.sicuan.presentation.component.SiCuanSecondaryButton
import com.example.sicuan.presentation.component.SiCuanTopAppBar
import com.example.sicuan.ui.theme.SiCuanDimens

@Composable
fun EducationScreen(
    userName: String,
    photoUrl: String?,
    onBack: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var subjectiveScore by remember { mutableIntStateOf(0) }
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
        SiCuanTopAppBar(
            userName = userName.split(" ").firstOrNull() ?: "Pengguna",
            photoUrl = photoUrl
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SiCuanDimens.SpacingLg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SiCuanDimens.SpacingLg)
        ) {
            when (step) {
                0 -> {
                    SiCuanCard {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Assignment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Kuis Literasi Keuangan",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mari cari tahu sejauh mana pemahamanmu tentang keuangan. Jawab 3 pertanyaan singkat ini untuk kalibrasi skor literasimu!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        SiCuanPrimaryButton(text = "Mulai Kuis", onClick = { step = 1 })
                    }
                }
                1 -> {
                    SiCuanCard {
                        Text(
                            text = "Penilaian Subjektif",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingSm))
                        Text(
                            text = "Sebelum mulai, seberapa yakin Anda dengan tingkat pemahaman finansial Anda saat ini?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(SiCuanDimens.SpacingMd))

                        listOf(
                            Pair("Sangat Yakin (Saya sudah ahli)", 3),
                            Pair("Biasa Saja (Paham dasar-dasarnya)", 2),
                            Pair("Kurang Yakin (Masih pemula)", 1)
                        ).forEach { (answer, sScore) ->
                            SiCuanSecondaryButton(
                                text = answer,
                                onClick = {
                                    subjectiveScore = sScore
                                    step = 2
                                }
                            )
                        }
                    }
                }
                2 -> {
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
                                        step = 3
                                    }
                                }
                            )
                        }
                    }
                }
                3 -> {
                    val objectiveLevel = when (score) {
                        0, 1 -> 1
                        2 -> 2
                        else -> 3
                    }
                    val levelStr = when (objectiveLevel) {
                        1 -> "Pemula"
                        2 -> "Menengah"
                        else -> "Mahir"
                    }

                    val dunningKrugerStatus = when {
                        subjectiveScore > objectiveLevel -> "Overconfident"
                        subjectiveScore < objectiveLevel -> "Underconfident"
                        else -> "Accurate"
                    }

                    val feedbackText = when (dunningKrugerStatus) {
                        "Overconfident" -> "Hasil kalibrasi menunjukkan Anda merasa lebih paham dari kenyataan (Dunning-Kruger Effect). Jangan khawatir, mari perkuat dasar finansial Anda terlebih dahulu!"
                        "Underconfident" -> "Luar biasa! Ternyata pemahaman Anda jauh lebih baik dari yang Anda perkirakan. Mari tingkatkan ke materi yang lebih menantang!"
                        else -> "Tepat sekali! Pemahaman objektif Anda sejalan dengan ekspektasi Anda. Terus pertahankan performa belajarmu!"
                    }

                    SiCuanCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            text = "Kalibrasi Selesai",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Level Objektif: $levelStr ($score/3)\nLevel Subjektif: ${when(subjectiveScore) {3 -> "Tinggi"; 2 -> "Menengah"; else -> "Rendah"}}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = feedbackText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 20.sp
                        )
                    }

                    Text(
                        text = "Modul Rekomendasi",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (objectiveLevel == 1) {
                        ModuleItem("Dasar Budgeting 50/30/20", "Pelajari cara membagi gaji dengan benar.")
                        ModuleItem("Pentingnya Mencatat Pengeluaran", "Mulai kebiasaan baik dari hal kecil.")
                    } else if (objectiveLevel == 2) {
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
                        subjectiveScore = 0
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

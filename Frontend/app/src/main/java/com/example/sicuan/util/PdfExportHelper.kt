package com.example.sicuan.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.sicuan.presentation.viewmodel.ReportData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.util.Locale

object PdfExportHelper {

    fun generateReportPdf(context: Context, report: ReportData) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Formatting
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        
        // Background
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Header
        paint.color = Color.parseColor("#1B5E20")
        canvas.drawRect(0f, 0f, 595f, 100f, paint)

        paint.color = Color.WHITE
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(report.title, 40f, 50f, paint)

        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(report.subtitle, 40f, 75f, paint)

        // Financial Summary Section
        var startY = 150f
        paint.color = Color.BLACK
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Ringkasan Keuangan", 40f, startY, paint)

        startY += 40f
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // Pemasukan
        paint.color = Color.DKGRAY
        canvas.drawText("Total Pemasukan:", 40f, startY, paint)
        paint.color = Color.parseColor("#4CAF50") // Green
        canvas.drawText(formatter.format(report.totalIncome), 250f, startY, paint)

        // Pengeluaran
        startY += 30f
        paint.color = Color.DKGRAY
        canvas.drawText("Total Pengeluaran:", 40f, startY, paint)
        paint.color = Color.parseColor("#EF5350") // Red
        canvas.drawText(formatter.format(report.totalExpense), 250f, startY, paint)

        // Saldo
        startY += 30f
        paint.color = Color.DKGRAY
        canvas.drawText("Saldo Akhir:", 40f, startY, paint)
        paint.color = Color.parseColor("#42A5F5") // Blue
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(formatter.format(report.balance), 250f, startY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // Transaction Count
        startY += 30f
        paint.color = Color.DKGRAY
        canvas.drawText("Jumlah Transaksi:", 40f, startY, paint)
        paint.color = Color.BLACK
        canvas.drawText("${report.transactionCount}", 250f, startY, paint)

        // Top Categories Section
        if (report.topCategories.isNotEmpty()) {
            startY += 60f
            paint.color = Color.BLACK
            paint.textSize = 18f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Kategori Pengeluaran Teratas", 40f, startY, paint)

            startY += 30f
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            report.topCategories.forEach { category ->
                startY += 30f
                paint.color = Color.DKGRAY
                canvas.drawText(category.name, 40f, startY, paint)
                
                paint.color = Color.parseColor("#EF5350")
                canvas.drawText(formatter.format(category.amount), 250f, startY, paint)
                
                paint.color = Color.BLACK
                canvas.drawText("${String.format("%.1f", category.percentage)}%", 450f, startY, paint)
            }
        }

        // Footer
        paint.color = Color.GRAY
        paint.textSize = 10f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Dicetak dari Aplikasi SiCuan", 595f / 2, 800f, paint)

        pdfDocument.finishPage(page)

        savePdfToDownloads(context, pdfDocument, "Laporan_SiCuan_${report.id}.pdf")
        pdfDocument.close()
    }

    private fun savePdfToDownloads(context: Context, document: PdfDocument, fileName: String) {
        try {
            val outputStream: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                outputStream = FileOutputStream(file)
            }

            outputStream?.use {
                document.writeTo(it)
                Toast.makeText(context, "Laporan berhasil diunduh ke Downloads", Toast.LENGTH_LONG).show()
            } ?: run {
                Toast.makeText(context, "Gagal membuat file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal mengunduh laporan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.example.sicuan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor

object PdfHelper {
    fun renderPdfToBitmap(context: Context, uri: Uri): Bitmap? {
        var fileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null
        
        return try {
            fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            if (fileDescriptor != null) {
                pdfRenderer = PdfRenderer(fileDescriptor)
                
                if (pdfRenderer.pageCount > 0) {
                    val page = pdfRenderer.openPage(0)
                    
                    // Render with high resolution for OCR (e.g. 2x standard density)
                    val density = context.resources.displayMetrics.density
                    val width = (page.width * density * 2).toInt()
                    val height = (page.height * density * 2).toInt()
                    
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    
                    // Fill white background (PDFs are often transparent)
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    
                    bitmap
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdfRenderer?.close()
            fileDescriptor?.close()
        }
    }
}

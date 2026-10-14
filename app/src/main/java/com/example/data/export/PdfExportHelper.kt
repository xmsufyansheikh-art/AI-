package com.example.data.export

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportHelper {

    fun generateAndSharePdf(
        context: Context,
        title: String,
        content: String,
        author: String = "Gulabo AI"
    ): Uri? {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // Standard A4 width in postscript points
            val pageHeight = 842 // Standard A4 height in postscript points

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint()

            // 1. Header Banner (Deep Blue #0C1A30)
            paint.color = Color.rgb(12, 26, 48)
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paint)

            // Header Rose Accent Line (#F43F5E)
            paint.color = Color.rgb(244, 63, 94)
            canvas.drawRect(0f, 86f, pageWidth.toFloat(), 90f, paint)

            // Header Title
            paint.color = Color.WHITE
            paint.textSize = 20f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("GULABO AI • INTELLIGENT REPORT", 36f, 45f, paint)

            // Header Subtitle / Date
            paint.textSize = 10f
            paint.color = Color.rgb(148, 163, 184)
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            canvas.drawText("Generated on: $dateStr  |  Created by: $author", 36f, 68f, paint)

            // 2. Document Title
            paint.color = Color.rgb(15, 23, 42)
            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val safeTitle = if (title.isBlank()) "Gulabo AI Generated Document" else title
            canvas.drawText(safeTitle, 36f, 130f, paint)

            // Divider line below title
            paint.color = Color.rgb(226, 232, 240)
            paint.strokeWidth = 1f
            canvas.drawLine(36f, 142f, (pageWidth - 36).toFloat(), 142f, paint)

            // 3. Document Body Content (Line wrapping)
            paint.color = Color.rgb(30, 41, 59)
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            var y = 170f
            val maxLineWidth = pageWidth - 72 // 36 margin on each side
            val lines = content.split("\n")

            for (paragraph in lines) {
                if (y > pageHeight - 60) break // Fit to single page or expand

                val words = paragraph.split(" ")
                var currentLine = StringBuilder()

                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val textWidth = paint.measureText(testLine)

                    if (textWidth < maxLineWidth) {
                        currentLine = StringBuilder(testLine)
                    } else {
                        canvas.drawText(currentLine.toString(), 36f, y, paint)
                        y += 18f
                        currentLine = StringBuilder(word)
                        if (y > pageHeight - 60) break
                    }
                }
                if (currentLine.isNotEmpty() && y <= pageHeight - 60) {
                    canvas.drawText(currentLine.toString(), 36f, y, paint)
                    y += 24f // paragraph gap
                }
            }

            // 4. Footer
            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText("Gulabo AI — Multilingual Universal Intelligence System", 36f, (pageHeight - 30).toFloat(), paint)

            pdfDocument.finishPage(page)

            // Save PDF to cache directory
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timestamp = System.currentTimeMillis()
            val pdfFile = File(exportDir, "Gulabo_AI_Doc_$timestamp.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Get FileProvider Uri
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            // Share Intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, safeTitle)
                putExtra(Intent.EXTRA_TEXT, "Here is a document generated by Gulabo AI.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Gulabo AI PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })

            Toast.makeText(context, "PDF successfully created & ready to export!", Toast.LENGTH_SHORT).show()
            return uri
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
            return null
        }
    }
}

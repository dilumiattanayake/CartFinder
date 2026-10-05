package com.sjay.cartfinder.phi

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
import com.sjay.cartfinder.data.model.Certificate
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfDownloader {
    fun downloadCertificatePdf(context: Context, cert: Certificate, stallName: String, stallAddress: String) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Background
            paint.color = Color.rgb(248, 249, 250)
            canvas.drawRect(0f, 0f, 595f, 842f, paint)

            // Border
            paint.color = Color.rgb(39, 174, 96)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 10f
            canvas.drawRect(20f, 20f, 575f, 822f, paint)

            paint.style = Paint.Style.FILL

            // Title
            paint.color = Color.BLACK
            paint.textSize = 36f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("PHI Hygiene Verified", 297f, 100f, paint)

            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = Color.DKGRAY
            canvas.drawText("Ministry of Health & Sanitation Sri Lanka", 297f, 130f, paint)

            // Stall Details
            paint.color = Color.BLACK
            paint.textSize = 24f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(stallName, 297f, 220f, paint)

            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = Color.DKGRAY
            canvas.drawText(stallAddress, 297f, 250f, paint)

            // Grade
            paint.color = Color.rgb(39, 174, 96)
            paint.textSize = 80f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(cert.grade, 297f, 380f, paint)

            paint.color = Color.BLACK
            paint.textSize = 20f
            canvas.drawText("Sanitation Rating Score: ${cert.score} / 100", 297f, 430f, paint)

            // Registration & Dates
            paint.textSize = 14f
            val regNum = cert.registrationNumber.ifEmpty { "MOH-MLB-2026-${cert.stallId.take(4)}" }
            canvas.drawText("Registration No: $regNum", 297f, 500f, paint)
            
            val formatter = SimpleDateFormat("MMMM yyyy", Locale.US)
            val expiryStr = formatter.format(Date(cert.expiryDate))
            canvas.drawText("Valid Until: $expiryStr", 297f, 530f, paint)
            canvas.drawText("Status: ${cert.status}", 297f, 560f, paint)

            // Footer
            paint.textSize = 12f
            paint.color = Color.GRAY
            canvas.drawText("Official Certificate issued by PHI", 297f, 750f, paint)

            pdfDocument.finishPage(page)

            // Save to Downloads
            val fileName = "PHI_Certificate_${stallName.replace(" ", "_")}.pdf"
            var outputStream: OutputStream? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    outputStream = resolver.openOutputStream(uri)
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(downloadsDir, fileName)
                outputStream = java.io.FileOutputStream(file)
            }

            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                outputStream.close()
                pdfDocument.close()
                Toast.makeText(context, "Certificate downloaded to Downloads folder", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
                pdfDocument.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error downloading certificate: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

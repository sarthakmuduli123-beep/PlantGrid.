package com.example.plantgrid.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.plantgrid.data.local.TelemetryLogEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class TelemetryExportEngine(private val context: Context) {

    fun exportToCsv(logs: List<TelemetryLogEntity>): Uri? {
        val fileName = "telemetry_report_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        try {
            FileOutputStream(file).use { out ->
                out.write("Timestamp,NodeID,Voltage(mV),Moisture(%),N,P,K\n".toByteArray())
                logs.forEach { log ->
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(log.timestamp))
                    val row = "$date,${log.nodeId},${log.microVoltage},${log.moisture},${log.nitrogen},${log.phosphorus},${log.potassium}\n"
                    out.write(row.toByteArray())
                }
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportToPdf(logs: List<TelemetryLogEntity>): Uri? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        paint.color = Color.BLACK
        paint.textSize = 18f
        canvas.drawText("BioNode Agro - Field Telemetry Report", 50f, 50f, paint)

        paint.textSize = 12f
        var y = 100f
        logs.take(30).forEach { log ->
            val text = "${SimpleDateFormat("HH:mm", Locale.US).format(Date(log.timestamp))} | Node: ${log.nodeId} | V: ${log.microVoltage} | M: ${log.moisture}"
            canvas.drawText(text, 50f, y, paint)
            y += 20f
        }

        document.finishPage(page)

        val fileName = "telemetry_report_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)
        try {
            document.writeTo(FileOutputStream(file))
            document.close()
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            return null
        }
    }

    fun shareFile(uri: Uri, type: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            this.type = type
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report"))
    }
}

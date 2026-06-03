package com.imr.example.newsmartykotlin.core.utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.imr.example.newsmartykotlin.domain.model.EraseStroke
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object BitmapUtils {

    fun loadBitmap(context: Context, path: String): Bitmap {
        return if (path.startsWith("http")) {
            val connection = URL(path).openConnection() as HttpURLConnection
            connection.connect()
            BitmapFactory.decodeStream(connection.inputStream)
        } else {
            context.contentResolver.openInputStream(Uri.parse(path)).use {
                BitmapFactory.decodeStream(it)
            }
        }
    }

    fun mergeSuitAndFace(
        suitBitmap: Bitmap?,
        faceBitmap: Bitmap
    ): Bitmap {
        val width = suitBitmap?.width ?: faceBitmap.width
        val height = suitBitmap?.height ?: faceBitmap.height

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        suitBitmap?.let {
            val suitRect = Rect(0, 0, width, height)
            canvas.drawBitmap(it, null, suitRect, null)
        }

        val faceWidth = width * 0.36f
        val faceHeight = faceWidth * faceBitmap.height / faceBitmap.width

        val left = (width - faceWidth) / 2f
        val top = height * 0.20f

        val faceRect = RectF(
            left,
            top,
            left + faceWidth,
            top + faceHeight
        )

        canvas.drawBitmap(faceBitmap, null, faceRect, Paint(Paint.ANTI_ALIAS_FLAG))

        return output
    }

    fun applyEraseStrokes(
        source: Bitmap,
        strokes: List<EraseStroke>
    ): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        strokes.forEach { stroke ->
            paint.strokeWidth = stroke.brushSize

            val path = Path()

            stroke.points.forEachIndexed { index, point ->
                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }

            canvas.drawPath(path, paint)
        }

        return result
    }

    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap
    ): String {
        val dir = File(context.cacheDir, "eraser_result")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "erased_${System.currentTimeMillis()}.png")

        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        return Uri.fromFile(file).toString()
    }
}
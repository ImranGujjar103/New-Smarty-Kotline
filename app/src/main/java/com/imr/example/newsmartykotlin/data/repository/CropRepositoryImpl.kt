package com.imr.example.newsmartykotlin.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import com.imr.example.newsmartykotlin.domain.repository.CropRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt
import androidx.core.net.toUri

class CropRepositoryImpl(
    private val context: Context
) : CropRepository {

    override suspend fun cropImage(
        sourceUri: String,
        imageBounds: RectF,
        cropRect: RectF
    ): String {
        return withContext(Dispatchers.IO) {
            val bitmap = context.contentResolver.openInputStream(sourceUri.toUri()).use { input ->
                BitmapFactory.decodeStream(input)
            } ?: throw IllegalStateException("Unable to decode image")

            val scaleX = bitmap.width / imageBounds.width()
            val scaleY = bitmap.height / imageBounds.height()

            val left = ((cropRect.left - imageBounds.left) * scaleX)
                .roundToInt()
                .coerceIn(0, bitmap.width - 1)

            val top = ((cropRect.top - imageBounds.top) * scaleY)
                .roundToInt()
                .coerceIn(0, bitmap.height - 1)

            val right = ((cropRect.right - imageBounds.left) * scaleX)
                .roundToInt()
                .coerceIn(left + 1, bitmap.width)

            val bottom = ((cropRect.bottom - imageBounds.top) * scaleY)
                .roundToInt()
                .coerceIn(top + 1, bitmap.height)

            val croppedBitmap = Bitmap.createBitmap(
                bitmap,
                left,
                top,
                right - left,
                bottom - top
            )

            val outputDir = File(context.cacheDir, "cropped_faces")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val outputFile = File(
                outputDir,
                "cropped_face_${System.currentTimeMillis()}.png"
            )

            FileOutputStream(outputFile).use { output ->
                croppedBitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    output
                )
            }

            if (!bitmap.isRecycled) bitmap.recycle()
            if (!croppedBitmap.isRecycled) croppedBitmap.recycle()

            Uri.fromFile(outputFile).toString()
        }
    }
}
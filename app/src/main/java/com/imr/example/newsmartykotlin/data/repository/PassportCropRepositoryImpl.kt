package com.imr.example.newsmartykotlin.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import com.imr.example.newsmartykotlin.core.utils.CacheImageFileManager
import com.imr.example.newsmartykotlin.domain.repository.PassportCropRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import androidx.core.graphics.scale

class PassportCropRepositoryImpl(
    private val context: Context,
    private val cacheImageFileManager: CacheImageFileManager
) : PassportCropRepository {

    override suspend fun cropPassportImage(
        sourceUri: String,
        imageBounds: RectF,
        cropRect: RectF,
        outputWidth: Int,
        outputHeight: Int
    ): String = withContext(Dispatchers.IO) {
        val bitmap = context.contentResolver
            .openInputStream(Uri.parse(sourceUri))
            .use { input ->
                BitmapFactory.decodeStream(input)
            } ?: error("Unable to read image")

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

        val cropped = Bitmap.createBitmap(
            bitmap,
            left,
            top,
            right - left,
            bottom - top
        )

        val finalBitmap = cropped.scale(outputWidth, outputHeight)

        cacheImageFileManager.saveBitmapToCache(
            bitmap = finalBitmap,
            fileName = "${System.currentTimeMillis()}.jpg"
        )
    }
}
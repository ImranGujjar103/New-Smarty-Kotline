package com.imr.example.newsmartykotlin.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.imr.example.newsmartykotlin.core.utils.ImageSaver
import com.imr.example.newsmartykotlin.domain.repository.BackgroundTextRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class BackgroundTextRepositoryImpl(
    private val context: Context
) : BackgroundTextRepository {

    override suspend fun saveBitmapToCache(bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            val file = File(
                context.cacheDir,
                "background_text_${System.currentTimeMillis()}.png"
            )

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
                )
            }

            file.absolutePath
        }
    }

    override suspend fun saveBitmapToGallery(bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            val uri = ImageSaver.saveBitmapToPictures(
                context = context,
                bitmap = bitmap,
                prefix = "Suit"
            )
            uri?.toString() ?: ""
        }
    }
}
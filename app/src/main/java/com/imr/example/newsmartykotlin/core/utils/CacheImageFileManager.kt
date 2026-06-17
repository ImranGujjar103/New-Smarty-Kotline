package com.imr.example.newsmartykotlin.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class CacheImageFileManager(
    private val context: Context
) {

    fun createCameraImageUri(): Uri {
        val file = File(
            context.cacheDir,
            "${System.currentTimeMillis()}.jpg"
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    fun saveBitmapToCache(
        bitmap: Bitmap,
        fileName: String = "${System.currentTimeMillis()}.jpg"
    ): String {
        val file = File(context.cacheDir, fileName)

        file.outputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output)
        }

        return Uri.fromFile(file).toString()
    }
}
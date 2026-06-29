package com.imr.example.newsmartykotlin.core.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.imr.example.newsmartykotlin.R
import java.io.File
import java.io.OutputStream

object ImageSaver {

    fun saveImageToPictures(
        context: Context,
        sourceUri: String,
        prefix: String = "Passport"
    ): Uri? {
        return saveToPictures(context, prefix) { output ->
            context.contentResolver.openInputStream(sourceUri.toUri())?.use { input ->
                input.copyTo(output)
            }
        }
    }

    fun saveBitmapToPictures(
        context: Context,
        bitmap: Bitmap,
        prefix: String = "Suit"
    ): Uri? {
        return saveToPictures(context, prefix) { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }

    private fun saveToPictures(
        context: Context,
        prefix: String,
        writeAction: (OutputStream) -> Unit
    ): Uri? {
        return runCatching {
            val appFolder = context.getString(R.string.app_name)
            val fileName = "${prefix}_${System.currentTimeMillis()}.png"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + appFolder
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val destinationUri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            resolver.openOutputStream(destinationUri)?.use { output ->
                writeAction(output)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(destinationUri, contentValues, null, null)
            }

            destinationUri
        }.getOrNull()
    }
}

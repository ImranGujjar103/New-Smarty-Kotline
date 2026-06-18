package com.imr.example.newsmartykotlin.core.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.imr.example.newsmartykotlin.R
import java.io.File

object ImageSaver {

    fun saveImageToPictures(
        context: Context,
        sourceUri: String
    ): Uri? {

        return runCatching {

            val appFolder =
                context.getString(R.string.app_name)

            val fileName =
                "Passport_${System.currentTimeMillis()}.png"

            val contentValues = ContentValues().apply {

                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    fileName
                )

                put(
                    MediaStore.Images.Media.MIME_TYPE,
                    "image/png"
                )

                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + appFolder
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver

            val destinationUri =
                resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: return null

            resolver.openOutputStream(destinationUri)?.use { output ->

                resolver.openInputStream(sourceUri.toUri())?.use { input ->
                    input.copyTo(output)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                contentValues.clear()

                contentValues.put(
                    MediaStore.Images.Media.IS_PENDING,
                    0
                )

                resolver.update(
                    destinationUri,
                    contentValues,
                    null,
                    null
                )
            }

            destinationUri

        }.getOrNull()
    }
}
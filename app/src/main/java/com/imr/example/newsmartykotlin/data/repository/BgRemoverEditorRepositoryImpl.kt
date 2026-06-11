package com.imr.example.newsmartykotlin.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import com.imr.example.newsmartykotlin.domain.repository.BgRemoverEditorRepository
import com.imr.example.newsmartykotlin.presentation.bgremovereditor.BgEditorBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class BgRemoverEditorRepositoryImpl(
    private val context: Context
) : BgRemoverEditorRepository {

    override suspend fun exportImage(
        removedImageUri: String,
        background: BgEditorBackground
    ): String = withContext(Dispatchers.IO) {
        val foreground = loadBitmap(removedImageUri)

        val outputBitmap = Bitmap.createBitmap(
            foreground.width,
            foreground.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(outputBitmap)

        when (background) {
            BgEditorBackground.Gallery,
            BgEditorBackground.Transparent -> {
                canvas.drawColor(Color.TRANSPARENT)
            }

            is BgEditorBackground.ColorBackground -> {
                canvas.drawColor(background.color.toInt())
            }

            is BgEditorBackground.DrawableBackground -> {
                val bg = BitmapFactory.decodeResource(
                    context.resources,
                    background.resId
                )

                val scaledBg = Bitmap.createScaledBitmap(
                    bg,
                    foreground.width,
                    foreground.height,
                    true
                )

                canvas.drawBitmap(scaledBg, 0f, 0f, null)
            }
            else -> {

            }
        }

        canvas.drawBitmap(foreground, 0f, 0f, null)

        val dir = File(context.filesDir, "bg_remover_exports")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "bg_remover_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { output ->
            outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }

        file.absolutePath
    }

    private fun loadBitmap(uriString: String): Bitmap {
        val uri = Uri.parse(uriString)

        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        } ?: BitmapFactory.decodeFile(uriString)
        ?: error("Unable to load image")
    }
}
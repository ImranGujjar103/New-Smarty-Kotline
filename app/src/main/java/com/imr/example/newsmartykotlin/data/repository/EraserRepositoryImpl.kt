package com.imr.example.newsmartykotlin.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.imr.example.newsmartykotlin.core.utils.BitmapUtils
import com.imr.example.newsmartykotlin.domain.model.EraseStroke
import com.imr.example.newsmartykotlin.domain.repository.EraserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EraserRepositoryImpl(
    private val context: Context
) : EraserRepository {

    override suspend fun createPreviewBitmap(
        faceImageUri: String
    ): Bitmap = withContext(Dispatchers.IO) {
        BitmapUtils.loadBitmap(context, faceImageUri)


    }


    override suspend fun saveErasedBitmap(
        previewBitmap: Bitmap,
        strokes: List<EraseStroke>
    ): String = withContext(Dispatchers.IO) {
        val erased = BitmapUtils.applyEraseStrokes(
            source = previewBitmap,
            strokes = strokes
        )

        BitmapUtils.saveBitmapToCache(
            context = context,
            bitmap = erased
        )
    }

    override suspend fun saveBitmap(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        BitmapUtils.saveBitmapToCache(
            context = context,
            bitmap = bitmap
        )
    }
}
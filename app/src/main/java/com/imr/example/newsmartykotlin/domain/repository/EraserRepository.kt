package com.imr.example.newsmartykotlin.domain.repository

import android.graphics.Bitmap
import com.imr.example.newsmartykotlin.domain.model.EraseStroke

interface EraserRepository {

    suspend fun createPreviewBitmap(
        faceImageUri: String
    ): Bitmap

    suspend fun saveErasedBitmap(
        sourceBitmap: Bitmap,
        strokes: List<EraseStroke>
    ): String

    suspend fun saveBitmap(bitmap: Bitmap): String
}
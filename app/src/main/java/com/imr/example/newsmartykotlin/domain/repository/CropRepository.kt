package com.imr.example.newsmartykotlin.domain.repository

import android.graphics.RectF

interface CropRepository {

    suspend fun cropImage(
        sourceUri: String,
        imageBounds: RectF,
        cropRect: RectF
    ): String
}
package com.imr.example.newsmartykotlin.domain.repository

import android.graphics.RectF

interface PassportCropRepository {

    suspend fun cropPassportImage(
        sourceUri: String,
        imageBounds: RectF,
        cropRect: RectF,
        outputWidth: Int,
        outputHeight: Int
    ): String
}
package com.imr.example.newsmartykotlin.domain.usecase.passport

import android.graphics.RectF
import com.imr.example.newsmartykotlin.domain.repository.PassportCropRepository

class CropPassportImageUseCase(
    private val repository: PassportCropRepository
) {
    suspend operator fun invoke(
        sourceUri: String,
        imageBounds: RectF,
        cropRect: RectF,
        outputWidth: Int,
        outputHeight: Int
    ): String {
        return repository.cropPassportImage(
            sourceUri = sourceUri,
            imageBounds = imageBounds,
            cropRect = cropRect,
            outputWidth = outputWidth,
            outputHeight = outputHeight
        )
    }
}
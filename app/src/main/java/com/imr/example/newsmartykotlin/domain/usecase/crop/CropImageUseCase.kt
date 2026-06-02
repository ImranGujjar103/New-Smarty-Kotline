package com.imr.example.newsmartykotlin.domain.usecase.crop

import android.graphics.RectF
import com.imr.example.newsmartykotlin.domain.repository.CropRepository

class CropImageUseCase(
    private val repository: CropRepository
) {

    suspend operator fun invoke(
        sourceUri: String,
        imageBounds: RectF,
        cropRect: RectF
    ): String {
        return repository.cropImage(
            sourceUri = sourceUri,
            imageBounds = imageBounds,
            cropRect = cropRect
        )
    }
}
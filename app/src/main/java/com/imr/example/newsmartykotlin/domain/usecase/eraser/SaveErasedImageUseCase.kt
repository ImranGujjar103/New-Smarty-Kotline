package com.imr.example.newsmartykotlin.domain.usecase.eraser

import android.graphics.Bitmap
import com.imr.example.newsmartykotlin.domain.model.EraseStroke
import com.imr.example.newsmartykotlin.domain.repository.EraserRepository

class SaveErasedImageUseCase(
    private val repository: EraserRepository
) {
    suspend operator fun invoke(
        previewBitmap: Bitmap,
        strokes: List<EraseStroke>
    ): String {
        return repository.saveErasedBitmap(previewBitmap, strokes)
    }
}
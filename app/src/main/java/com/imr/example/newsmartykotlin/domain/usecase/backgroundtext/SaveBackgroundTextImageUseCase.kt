package com.imr.example.newsmartykotlin.domain.usecase.backgroundtext


import android.graphics.Bitmap
import com.imr.example.newsmartykotlin.domain.repository.BackgroundTextRepository

class SaveBackgroundTextImageUseCase(
    private val repository: BackgroundTextRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): String {
        return repository.saveBitmapToCache(bitmap)
    }
}
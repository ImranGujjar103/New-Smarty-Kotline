package com.imr.example.newsmartykotlin.domain.usecase.eraser

import com.imr.example.newsmartykotlin.domain.repository.EraserRepository


class CreateEraserPreviewUseCase(
    private val repository: EraserRepository
) {
    suspend operator fun invoke(
        faceImageUri: String,
    ) = repository.createPreviewBitmap(faceImageUri)
}
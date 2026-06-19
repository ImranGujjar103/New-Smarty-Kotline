package com.imr.example.newsmartykotlin.domain.usecase.bgremovereditor

import com.imr.example.newsmartykotlin.domain.repository.BgRemoverEditorRepository
import com.imr.example.newsmartykotlin.presentation.bgremovereditor.BgEditorBackground

class ExportBgRemoverImageUseCase(
    private val repository: BgRemoverEditorRepository
) {
    suspend operator fun invoke(
        removedImageUri: String,
        background: BgEditorBackground,
        flipX: Float
    ): String {
        return repository.exportImage(
            removedImageUri = removedImageUri,
            background = background,
            flipX = flipX
        )
    }
}
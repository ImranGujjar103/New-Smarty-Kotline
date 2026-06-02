package com.imr.example.newsmartykotlin.domain.usecase.photoeditor

import com.imr.example.newsmartykotlin.domain.model.SuitItem
import com.imr.example.newsmartykotlin.domain.repository.PhotoEditorRepository

class GetPhotoEditorSuitUseCase(
    private val repository: PhotoEditorRepository
) {
    suspend operator fun invoke(suitId: String): SuitItem? {
        return repository.getSuitById(suitId)
    }
}
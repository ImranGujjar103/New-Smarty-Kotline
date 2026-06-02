package com.imr.example.newsmartykotlin.domain.usecase.gallery

import com.imr.example.newsmartykotlin.domain.repository.GalleryRepository

class ResetPermissionDenyCountUseCase(
    private val repository: GalleryRepository
) {

    suspend operator fun invoke() {
        repository.resetPermissionDenyCount()
    }
}
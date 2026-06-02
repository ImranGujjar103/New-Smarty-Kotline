package com.imr.example.newsmartykotlin.domain.usecase.gallery

import com.imr.example.newsmartykotlin.domain.repository.GalleryRepository

class IncrementPermissionDenyCountUseCase(
    private val repository: GalleryRepository
) {

    suspend operator fun invoke() {
        repository.incrementPermissionDenyCount()
    }
}
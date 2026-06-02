package com.imr.example.newsmartykotlin.domain.usecase.gallery

import com.imr.example.newsmartykotlin.domain.model.GalleryImage
import com.imr.example.newsmartykotlin.domain.repository.GalleryRepository

class GetGalleryImagesUseCase(
    private val repository: GalleryRepository
) {

    suspend operator fun invoke(): List<GalleryImage> {
        return repository.getGalleryImages()
    }
}
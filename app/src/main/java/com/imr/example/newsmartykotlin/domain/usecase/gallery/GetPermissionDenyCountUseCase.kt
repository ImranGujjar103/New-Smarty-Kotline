package com.imr.example.newsmartykotlin.domain.usecase.gallery

import com.imr.example.newsmartykotlin.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow

class GetPermissionDenyCountUseCase(
    private val repository: GalleryRepository
) {

    operator fun invoke(): Flow<Int> {
        return repository.getPermissionDenyCount()
    }
}
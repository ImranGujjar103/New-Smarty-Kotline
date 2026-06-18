package com.imr.example.newsmartykotlin.domain.usecase.creation

import com.imr.example.newsmartykotlin.domain.model.Creation
import com.imr.example.newsmartykotlin.domain.repository.CreationRepository

class GetCreationsUseCase(
    private val repository: CreationRepository
) {
    suspend operator fun invoke(): List<Creation> {
        return repository.getCreations()
    }
}

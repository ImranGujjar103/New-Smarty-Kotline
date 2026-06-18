package com.imr.example.newsmartykotlin.domain.usecase.creation

import com.imr.example.newsmartykotlin.domain.model.Creation
import com.imr.example.newsmartykotlin.domain.repository.CreationRepository

class DeleteCreationUseCase(
    private val repository: CreationRepository
) {
    suspend operator fun invoke(creation: Creation): Boolean {
        return repository.deleteCreation(creation)
    }
}

package com.imr.example.newsmartykotlin.domain.usecase

import com.imr.example.newsmartykotlin.domain.model.SuitCategory
import com.imr.example.newsmartykotlin.domain.repository.SuitRepository

class GetSuitCategoriesUseCase(
    private val repository: SuitRepository
) {
    suspend operator fun invoke(): List<SuitCategory> {
        return repository.getSuitCategories()
    }
}
package com.imr.example.newsmartykotlin.domain.usecase.bgremove

import com.imr.example.newsmartykotlin.domain.repository.BgRemoveRepository

class RemoveBackgroundUseCase(
    private val repository: BgRemoveRepository
) {

    suspend operator fun invoke(
        imageUri: String
    ): String {
        return repository.removeBackground(imageUri)
    }
}
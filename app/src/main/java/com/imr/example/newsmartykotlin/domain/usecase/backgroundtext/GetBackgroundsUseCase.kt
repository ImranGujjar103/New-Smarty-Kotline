package com.imr.example.newsmartykotlin.domain.usecase.backgroundtext

import com.imr.example.newsmartykotlin.domain.repository.BackgroundRepository


class GetBackgroundsUseCase(
    private val repository: BackgroundRepository
) {
    operator fun invoke() = repository.getBackgrounds()
}
package com.imr.example.newsmartykotlin.domain.usecase.home

import com.imr.example.newsmartykotlin.domain.model.HomeFeature
import com.imr.example.newsmartykotlin.domain.repository.HomeRepository

class GetHomeFeaturesUseCase(
    private val repository: HomeRepository
) {
    operator fun invoke(): List<HomeFeature> {
        return repository.getHomeFeatures()
    }
}
package com.imr.example.newsmartykotlin.domain.usecase.premium

import com.imr.example.newsmartykotlin.domain.repository.PremiumRepository

class FetchBillingProductsUseCase(
    private val repository: PremiumRepository
) {
    suspend operator fun invoke() {
        repository.fetchBillingProducts()
    }
}
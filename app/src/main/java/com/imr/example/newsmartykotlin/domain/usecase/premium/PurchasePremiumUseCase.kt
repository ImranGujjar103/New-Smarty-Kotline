package com.imr.example.newsmartykotlin.domain.usecase.premium

import com.imr.example.newsmartykotlin.domain.repository.PremiumRepository

class PurchasePremiumUseCase(
    private val repository: PremiumRepository
) {
    suspend operator fun invoke(productId: String) {
        repository.purchase(productId)
    }
}
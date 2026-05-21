package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.domain.model.PremiumLayoutType
import com.imr.example.newsmartykotlin.domain.model.PremiumPlan
import kotlinx.coroutines.flow.Flow

interface PremiumRepository {
    fun observePlans(): Flow<List<PremiumPlan>>
    fun observePremiumLayout(): Flow<PremiumLayoutType>
    fun observePurchaseState(): Flow<Boolean>

    suspend fun fetchBillingProducts()
    suspend fun purchase(productId: String)
}
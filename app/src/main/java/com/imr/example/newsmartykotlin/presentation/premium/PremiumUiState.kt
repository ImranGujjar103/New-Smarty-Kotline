package com.imr.example.newsmartykotlin.presentation.premium

import com.imr.example.newsmartykotlin.domain.model.PremiumLayoutType
import com.imr.example.newsmartykotlin.domain.model.PremiumPlan

data class PremiumUiState(
    val isLoading: Boolean = true,
    val layoutType: PremiumLayoutType = PremiumLayoutType.SINGLE_CARD,
    val plans: List<PremiumPlan> = emptyList(),
    val selectedProductId: String = "",
    val isPurchased: Boolean = false
)
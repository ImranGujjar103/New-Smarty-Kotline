package com.imr.example.newsmartykotlin.domain.model

data class PremiumPlan(
    val productId: String,
    val title: String,
    val price: String,
    val billingPeriod: String,
    val isSelected: Boolean = false,
    val hasTrial: Boolean = false,
    val trialInfo: String = "",
    val trialInfoAfter: String = ""
)

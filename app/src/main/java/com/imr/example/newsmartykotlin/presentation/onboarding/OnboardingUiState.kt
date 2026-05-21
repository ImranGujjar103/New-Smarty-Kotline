package com.imr.example.newsmartykotlin.presentation.onboarding

data class OnboardingUiState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPage> = onboardingPages
)
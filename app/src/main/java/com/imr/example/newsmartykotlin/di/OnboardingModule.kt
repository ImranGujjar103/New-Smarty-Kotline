package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.presentation.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel { OnboardingViewModel() }
}
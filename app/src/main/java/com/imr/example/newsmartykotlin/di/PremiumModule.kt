package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.core.utils.BillingManager
import com.imr.example.newsmartykotlin.data.repository.PremiumRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.PremiumRepository
import com.imr.example.newsmartykotlin.domain.usecase.premium.FetchBillingProductsUseCase
import com.imr.example.newsmartykotlin.domain.usecase.premium.ObservePremiumUiUseCase
import com.imr.example.newsmartykotlin.domain.usecase.premium.PurchasePremiumUseCase
import com.imr.example.newsmartykotlin.presentation.premium.PremiumViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val premiumModule = module {

    single {
        BillingManager(
            context = androidContext(),
            dataStorePrefs = get()
        )
    }

    single<PremiumRepository> {
        PremiumRepositoryImpl(
            prefs = get()
        )
    }

    factory { ObservePremiumUiUseCase(get()) }

    factory { FetchBillingProductsUseCase(get()) }

    factory { PurchasePremiumUseCase(get()) }

    viewModel {
        PremiumViewModel(
            observePremiumUiUseCase = get(),
            fetchBillingProductsUseCase = get(),
            purchasePremiumUseCase = get()
        )
    }
}
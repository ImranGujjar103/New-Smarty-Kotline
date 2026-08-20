package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.presentation.thankyou.ThankYouViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val thankYouModule = module {
    viewModel { ThankYouViewModel() }
}

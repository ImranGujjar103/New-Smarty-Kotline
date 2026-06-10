package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.presentation.saved.SavedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val savedModule = module {
    viewModel {
        SavedViewModel(
            savedStateHandle = get()
        )
    }
}
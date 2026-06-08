package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.BackgroundTextRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.BackgroundTextRepository
import com.imr.example.newsmartykotlin.domain.usecase.backgroundtext.SaveBackgroundTextImageUseCase
import com.imr.example.newsmartykotlin.presentation.backgroundtext.BackgroundTextViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val backgroundTextModule = module {
    single<BackgroundTextRepository> {
        BackgroundTextRepositoryImpl(
            context = androidContext()
        )
    }

    factory {
        SaveBackgroundTextImageUseCase(
            repository = get()
        )
    }

    viewModel {
        BackgroundTextViewModel(
            savedStateHandle = get(),
            saveBackgroundTextImageUseCase = get()
        )
    }
}
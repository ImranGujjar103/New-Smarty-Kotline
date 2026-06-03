package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.EraserRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.EraserRepository
import com.imr.example.newsmartykotlin.domain.usecase.eraser.CreateEraserPreviewUseCase
import com.imr.example.newsmartykotlin.domain.usecase.eraser.SaveErasedImageUseCase
import com.imr.example.newsmartykotlin.presentation.eraser.EraserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val eraserModule = module {
    single<EraserRepository> {
        EraserRepositoryImpl(
            context = androidContext()
        )
    }

    factory {
        CreateEraserPreviewUseCase(
            repository = get()
        )
    }

    factory {
        SaveErasedImageUseCase(
            repository = get()
        )
    }

    viewModel {
        EraserViewModel(
            savedStateHandle = get(),
            createEraserPreviewUseCase = get(),
            saveErasedImageUseCase = get()
        )
    }
}
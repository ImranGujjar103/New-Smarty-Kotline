package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.BgRemoverEditorRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.BgRemoverEditorRepository
import com.imr.example.newsmartykotlin.domain.usecase.bgremovereditor.ExportBgRemoverImageUseCase
import com.imr.example.newsmartykotlin.presentation.bgremovereditor.BgRemoverEditorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bgRemoverEditorModule = module {
    single<BgRemoverEditorRepository> {
        BgRemoverEditorRepositoryImpl(
            context = get()
        )
    }

    factory {
        ExportBgRemoverImageUseCase(
            repository = get()
        )
    }

    viewModel {
        BgRemoverEditorViewModel(
            savedStateHandle = get(),
            exportBgRemoverImageUseCase = get()
        )
    }
}
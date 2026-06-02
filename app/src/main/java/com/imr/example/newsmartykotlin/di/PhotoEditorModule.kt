package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.PhotoEditorRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.PhotoEditorRepository
import com.imr.example.newsmartykotlin.domain.usecase.photoeditor.GetPhotoEditorSuitUseCase
import com.imr.example.newsmartykotlin.presentation.photoeditor.PhotoEditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val photoEditorModule = module {

    single<PhotoEditorRepository> {
        PhotoEditorRepositoryImpl()
    }

    factory {
        GetPhotoEditorSuitUseCase(
            repository = get()
        )
    }

    viewModel {
        PhotoEditorViewModel(
            savedStateHandle = get(),
            getPhotoEditorSuitUseCase = get()
        )
    }
}
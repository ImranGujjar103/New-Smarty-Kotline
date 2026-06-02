package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.CropRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.CropRepository
import com.imr.example.newsmartykotlin.domain.usecase.crop.CropImageUseCase
import com.imr.example.newsmartykotlin.presentation.crop.CropFaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cropModule = module {

    single<CropRepository> {
        CropRepositoryImpl(
            context = androidContext()
        )
    }

    factory {
        CropImageUseCase(
            repository = get()
        )
    }

    viewModel {
        CropFaceViewModel(
            savedStateHandle = get(),
            cropImageUseCase = get()
        )
    }
}
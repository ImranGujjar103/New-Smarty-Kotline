package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.core.utils.CacheImageFileManager
import com.imr.example.newsmartykotlin.data.repository.PassportCropRepositoryImpl
import com.imr.example.newsmartykotlin.data.repository.PassportRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.PassportCropRepository
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository
import com.imr.example.newsmartykotlin.domain.usecase.passport.CropPassportImageUseCase
import com.imr.example.newsmartykotlin.presentation.passport.PassportCountryViewModel
import com.imr.example.newsmartykotlin.presentation.passport.PassportDetailViewModel
import com.imr.example.newsmartykotlin.presentation.passport.cropper.PassportCropperViewModel
import com.imr.example.newsmartykotlin.presentation.passport.result.PassportResultViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val passportModule = module {
    single<PassportRepository> { PassportRepositoryImpl() }

    viewModel { PassportCountryViewModel(get()) }
    single { CacheImageFileManager(androidContext()) }

    single<PassportCropRepository> {
        PassportCropRepositoryImpl(
            context = androidContext(),
            cacheImageFileManager = get()
        )
    }

    factory {
        CropPassportImageUseCase(
            repository = get()
        )
    }

    viewModel {
        PassportDetailViewModel(
            savedStateHandle = get(),
            repository = get()
        )
    }

    viewModel {
        PassportCropperViewModel(
            savedStateHandle = get(),
            passportRepository = get(),
            cropPassportImageUseCase = get()
        )
    }

    viewModel {
        PassportResultViewModel(
            savedStateHandle = get(),
            context = androidContext(),
            passportRepository = get()
        )
    }
}
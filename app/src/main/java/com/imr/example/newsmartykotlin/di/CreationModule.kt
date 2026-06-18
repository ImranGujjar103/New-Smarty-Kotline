package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.CreationRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.CreationRepository
import com.imr.example.newsmartykotlin.domain.usecase.creation.DeleteCreationUseCase
import com.imr.example.newsmartykotlin.domain.usecase.creation.GetCreationsUseCase
import com.imr.example.newsmartykotlin.presentation.creation.MyCreationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val creationModule = module {
    single<CreationRepository> { CreationRepositoryImpl(get()) }
    factory { GetCreationsUseCase(get()) }
    factory { DeleteCreationUseCase(get()) }
    viewModel { MyCreationViewModel(get(), get()) }
}

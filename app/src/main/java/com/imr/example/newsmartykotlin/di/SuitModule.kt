package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.SuitRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.SuitRepository
import com.imr.example.newsmartykotlin.domain.usecase.suits.GetSuitCategoriesUseCase
import com.imr.example.newsmartykotlin.presentation.suits.SuitViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val suitModule = module {

    single<SuitRepository> {
        SuitRepositoryImpl(
            context = get()
        )
    }

    factory {
        GetSuitCategoriesUseCase(
            repository = get()
        )
    }

    viewModel {
        SuitViewModel(
            getSuitCategoriesUseCase = get()
        )
    }
}
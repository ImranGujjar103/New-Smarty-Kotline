package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.HomeRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.HomeRepository
import com.imr.example.newsmartykotlin.domain.usecase.GetHomeFeaturesUseCase
import com.imr.example.newsmartykotlin.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {

    single<HomeRepository> {
        HomeRepositoryImpl()
    }

    factory {
        GetHomeFeaturesUseCase(
            repository = get()
        )
    }

    viewModel {
        HomeViewModel(
            getHomeFeaturesUseCase = get()
        )
    }
}
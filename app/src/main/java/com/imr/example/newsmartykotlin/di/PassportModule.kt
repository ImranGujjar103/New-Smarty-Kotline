package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.data.repository.PassportRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository
import com.imr.example.newsmartykotlin.presentation.passport.PassportCountryViewModel
import com.imr.example.newsmartykotlin.presentation.passport.PassportDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val passportModule = module {
    single<PassportRepository> { PassportRepositoryImpl() }

    viewModel { PassportCountryViewModel(get()) }
    viewModel { PassportDetailViewModel(get()) }
}
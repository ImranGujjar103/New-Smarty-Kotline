package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.presentation.language.LanguageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module

val languageModule = module {
    viewModel { LanguageViewModel(get()) }
}
package com.imr.example.newsmartykotlin.di

import com.imr.example.newsmartykotlin.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(get()) }
}

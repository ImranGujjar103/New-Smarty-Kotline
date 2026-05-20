package com.imr.example.newsmartykotlin.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.imr.example.newsmartykotlin.BuildConfig
import com.imr.example.newsmartykotlin.core.ads.AdLoadingController
import com.imr.example.newsmartykotlin.core.ads.AdManager
import com.imr.example.newsmartykotlin.core.ads.AdsConsentManager
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.data.model.AppConfig
import com.imr.example.newsmartykotlin.data.repository.AdRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.AdRepository
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val adModule = module {
    single { DataStorePrefs(androidContext()) }

    single { AdLoadingController() }
    single {
        FirebaseRemoteConfig.getInstance().apply {
            val fetchInterval = if (BuildConfig.DEBUG) 1L else 3600L

            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(fetchInterval)
                    .build()
            )

            val configKey = if (BuildConfig.DEBUG) {
                AdManager.AD_SETTING_DEBUG
            } else {
                AdManager.AD_SETTING_RELEASE
            }

            setDefaultsAsync(
                mapOf(configKey to Gson().toJson(AppConfig()))
            )
        }
    }

    single<AdRepository> { AdRepositoryImpl(get()) }
    single { AdsConsentManager(androidContext()) }

    viewModel {
        AdViewModel(
            adRepository = get(),
            dataStorePrefs = get(),
            context = androidContext()
        )
    }
}
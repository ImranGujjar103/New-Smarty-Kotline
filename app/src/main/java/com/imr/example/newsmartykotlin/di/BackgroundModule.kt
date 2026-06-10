package com.imr.example.newsmartykotlin.di


import com.imr.example.newsmartykotlin.core.network.ConnectivityNetworkMonitor
import com.imr.example.newsmartykotlin.core.network.NetworkMonitor
import com.imr.example.newsmartykotlin.data.local.AssetSheetReader
import com.imr.example.newsmartykotlin.data.mapper.BackgroundSheetTsvParser
import com.imr.example.newsmartykotlin.data.remote.BackgroundRemoteDataSource
import com.imr.example.newsmartykotlin.data.repository.BackgroundRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.BackgroundRepository
import com.imr.example.newsmartykotlin.domain.usecase.backgroundtext.GetBackgroundsUseCase
import com.imr.example.newsmartykotlin.presentation.backgroundtext.backgroundsheet.BackgroundViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val backgroundModule = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    single<NetworkMonitor> {
        ConnectivityNetworkMonitor(
            context = androidContext(),
            appScope = get()
        )
    }
    single {
        AssetSheetReader(androidContext())
    }
    single { BackgroundRemoteDataSource(get()) }

    single { BackgroundSheetTsvParser() }

    single<BackgroundRepository> {
        BackgroundRepositoryImpl(
            remoteDataSource = get(),
            parser = get()
        )
    }

    factory { GetBackgroundsUseCase(get()) }

    viewModel {
        BackgroundViewModel(
            get(),
            networkMonitor = get()
        )
    }
}
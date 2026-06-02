package com.imr.example.newsmartykotlin.di


import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.imr.example.newsmartykotlin.data.repository.BgRemoveRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.BgRemoveRepository
import com.imr.example.newsmartykotlin.domain.usecase.bgremove.RemoveBackgroundUseCase
import com.imr.example.newsmartykotlin.presentation.bgremove.BgRemoveViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bgRemoveModule = module {

    single<BgRemoveRepository> {
        BgRemoveRepositoryImpl(
            context = androidContext(),
            analyzer = MLAnalyzerFactory.getInstance().imageSegmentationAnalyzer
        )
    }

    factory {
        RemoveBackgroundUseCase(
            repository = get()
        )
    }

    viewModel {
        BgRemoveViewModel(
            savedStateHandle = get(),
            removeBackgroundUseCase = get()
        )
    }
}
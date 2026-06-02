package com.imr.example.newsmartykotlin.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.imr.example.newsmartykotlin.data.local.GalleryLocalDataSource
import com.imr.example.newsmartykotlin.data.local.PermissionDataStore
import com.imr.example.newsmartykotlin.data.repository.GalleryRepositoryImpl
import com.imr.example.newsmartykotlin.domain.repository.GalleryRepository
import com.imr.example.newsmartykotlin.domain.usecase.gallery.GetGalleryImagesUseCase
import com.imr.example.newsmartykotlin.domain.usecase.gallery.GetPermissionDenyCountUseCase
import com.imr.example.newsmartykotlin.domain.usecase.gallery.IncrementPermissionDenyCountUseCase
import com.imr.example.newsmartykotlin.domain.usecase.gallery.ResetPermissionDenyCountUseCase
import com.imr.example.newsmartykotlin.presentation.gallery.GalleryViewModel
import com.imr.example.newsmartykotlin.presentation.permission.GalleryPermissionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val galleryModule = module {

    single {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("gallery_permission_prefs")
        }
    }

    single {
        PermissionDataStore(
            dataStore = get()
        )
    }

    single {
        GalleryLocalDataSource(
            context = androidContext()
        )
    }

    single<GalleryRepository> {
        GalleryRepositoryImpl(
            galleryLocalDataSource = get(),
            permissionDataStore = get()
        )
    }

    factory {
        GetGalleryImagesUseCase(
            repository = get()
        )
    }

    factory {
        GetPermissionDenyCountUseCase(
            repository = get()
        )
    }

    factory {
        IncrementPermissionDenyCountUseCase(
            repository = get()
        )
    }

    factory {
        ResetPermissionDenyCountUseCase(
            repository = get()
        )
    }

    viewModel {
        GalleryViewModel(
            savedStateHandle = get(),
            getGalleryImagesUseCase = get()
        )
    }

    viewModel {
        GalleryPermissionViewModel(
            savedStateHandle = get(),
            getPermissionDenyCountUseCase = get(),
            incrementPermissionDenyCountUseCase = get(),
            resetPermissionDenyCountUseCase = get()
        )
    }
}
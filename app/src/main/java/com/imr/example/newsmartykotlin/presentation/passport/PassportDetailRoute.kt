package com.imr.example.newsmartykotlin.presentation.passport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PassportDetailRoute(
    countryId: String,
    selectedType: DocumentType,
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    viewModel: PassportDetailViewModel = koinViewModel()
) {
    val country = remember(countryId) {
        viewModel.getCountry(countryId)
    }

    country?.let {
        PassportDetailScreen(
            country = it,
            selectedType = selectedType,
            onBackClick = onBackClick,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick
        )
    }
}
package com.imr.example.newsmartykotlin.presentation.passport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import org.koin.compose.viewmodel.koinViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.imr.example.newsmartykotlin.core.utils.CacheImageFileManager
import org.koin.compose.koinInject

@Composable
fun PassportDetailRoute(
    countryId: String,
    selectedType: DocumentType,
    onBackClick: () -> Unit,
    onCameraImageCaptured: (String) -> Unit,
    onGalleryClick: () -> Unit,
    viewModel: PassportDetailViewModel = koinViewModel(),
    cacheImageFileManager: CacheImageFileManager = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    val country = remember(countryId) {
        viewModel.getCountry(countryId)
    }

    var cameraUri: Uri? = remember { null }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.toString()?.let { uri ->
                onCameraImageCaptured(uri)
            }
        }
    }

    country?.let {
        PassportDetailScreen(
            country = it,
            selectedType = selectedType,
            finalImageUri = uiState.finalImageUri,
            onBackClick = onBackClick,
            onCameraClick = {
                cameraUri = cacheImageFileManager.createCameraImageUri()
                cameraUri?.let { uri ->
                    cameraLauncher.launch(uri)
                }
            },
            onGalleryClick = onGalleryClick
        )
    }
}
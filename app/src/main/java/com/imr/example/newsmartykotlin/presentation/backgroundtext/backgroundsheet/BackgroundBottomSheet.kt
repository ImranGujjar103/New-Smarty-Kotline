package com.imr.example.newsmartykotlin.presentation.backgroundtext.backgroundsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundBottomSheet(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onBackgroundSelected: (String) -> Unit,
    viewModel: BackgroundViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        BackgroundSheetContent(
            uiState = uiState,
            onRetryClick = viewModel::loadBackgrounds,
            onGalleryClick = {
                onDismiss()
                onGalleryClick()
            },
            onBackgroundClick = { background ->
                onBackgroundSelected(background.imageUrl)
                onDismiss()
            },
            onCloseClick = {
                onDismiss()
            }
        )
    }
}
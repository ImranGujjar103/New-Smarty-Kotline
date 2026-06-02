package com.imr.example.newsmartykotlin.presentation.permission

data class GalleryPermissionUiState(
    val denyCount: Int = 0,
    val showUnlockDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val isPermissionGranted: Boolean = false
)
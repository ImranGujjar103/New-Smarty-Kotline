package com.imr.example.newsmartykotlin.presentation.passport

data class PassportDetailUiState(
    val finalImageUri: String? = null,
    val showPermissionDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val cameraDenyCount: Int = 0
)
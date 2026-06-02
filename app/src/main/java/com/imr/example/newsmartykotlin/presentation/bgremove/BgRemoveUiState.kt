package com.imr.example.newsmartykotlin.presentation.bgremove

data class BgRemoveUiState(
    val suitId: String = "",
    val croppedImageUri: String = "",
    val removedBgImageUri: String? = null,
    val progress: Float = 0.5f,
    val progressText: String = "50%",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
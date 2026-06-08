package com.imr.example.newsmartykotlin.presentation.backgroundtext

data class BackgroundTextUiState(
    val imagePath: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
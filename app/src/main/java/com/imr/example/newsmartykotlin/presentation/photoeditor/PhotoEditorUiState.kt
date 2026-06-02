package com.imr.example.newsmartykotlin.presentation.photoeditor

import com.imr.example.newsmartykotlin.domain.model.SuitItem

data class PhotoEditorUiState(
    val suitId: String = "",
    val faceImageUri: String = "",
    val suitItem: SuitItem? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
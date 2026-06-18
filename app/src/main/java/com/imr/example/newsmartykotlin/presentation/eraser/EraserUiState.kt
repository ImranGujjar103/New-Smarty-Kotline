package com.imr.example.newsmartykotlin.presentation.eraser

import android.graphics.Bitmap

data class EraserUiState(
    val faceImageUri: String = "",
    val previewBitmap: Bitmap? = null,
    val brushSize: Float = 45f,
    val brushOffset: Float = 90f,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,

    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val showResetDialog: Boolean = false
)
package com.imr.example.newsmartykotlin.presentation.crop

import android.graphics.RectF
import com.imr.example.newsmartykotlin.domain.model.CropAspectRatio

data class CropFaceUiState(
    val suitId: String = "",
    val imageUri: String = "",
    val selectedRatio: CropAspectRatio = CropAspectRatio.ORIGINAL,
    val isCropping: Boolean = false,
    val errorMessage: String? = null,
    val croppedImageUri: String? = null,
    val imageBounds: RectF? = null,
    val cropRect: RectF? = null
)
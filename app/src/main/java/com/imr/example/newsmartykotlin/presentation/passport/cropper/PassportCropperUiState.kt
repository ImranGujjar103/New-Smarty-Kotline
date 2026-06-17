package com.imr.example.newsmartykotlin.presentation.passport.cropper

import android.graphics.RectF

data class PassportCropperUiState(
    val imageUri: String = "",
    val countryId: String = "",
    val documentType: String = "",
    val pixelText: String = "",
    val inchText: String = "",
    val isCropping: Boolean = false,
    val errorMessage: String? = null,
    val imageBounds: RectF? = null,
    val cropRect: RectF? = null
)
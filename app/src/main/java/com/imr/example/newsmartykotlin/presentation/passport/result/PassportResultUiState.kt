package com.imr.example.newsmartykotlin.presentation.passport.result

data class PassportResultUiState(
    val imageUri: String = "",
    val countryId: String = "",
    val documentType: String = "",
    val pixelText: String = "",
    val inchText: String = "",
    val fileSizeText: String = "",
    val dpiText: String = "300 DPI"
)
package com.imr.example.newsmartykotlin.presentation.passport.background

import androidx.compose.ui.graphics.Color

data class PassportBackgroundUiState(
    val imageUri: String = "",
    val selectedColor: Color = Color.White,
    val isBottomSheetExpanded: Boolean = true
)
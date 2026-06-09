package com.imr.example.newsmartykotlin.presentation.backgroundtext.model

import androidx.compose.ui.graphics.Color

sealed class TextColorItem {
    data object CustomPicker : TextColorItem()

    data class Preset(
        val color: Color
    ) : TextColorItem()
}
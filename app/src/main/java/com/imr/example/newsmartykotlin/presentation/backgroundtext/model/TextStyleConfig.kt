package com.imr.example.newsmartykotlin.presentation.backgroundtext.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

data class TextFontOption(

    val id: String,
    val title: String,
    val fontFamily: FontFamily
)

data class TextStyleConfig(
    val fontId: String = "sf_pro_bold",
    val color: Color = Color.White,
    val shadowEnabled: Boolean = false,
    val textAlign: TextAlign = TextAlign.Center,
    val fontSize: Float = 42f,
    val isBold: Boolean = true
)
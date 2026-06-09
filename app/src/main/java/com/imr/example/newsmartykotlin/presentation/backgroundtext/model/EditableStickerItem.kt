package com.imr.example.newsmartykotlin.presentation.backgroundtext.model

import androidx.compose.ui.geometry.Offset

enum class StickerType {
    Emoji,
    Text
}

data class EditableStickerItem(
    val id: Long,
    val value: String,
    val type: StickerType,
    val offset: Offset = Offset.Zero,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val isSelected: Boolean = true,
    val textStyle: TextStyleConfig = TextStyleConfig()
) {
    val isEmoji: Boolean get() = type == StickerType.Emoji
    val isText: Boolean get() = type == StickerType.Text
}
package com.imr.example.newsmartykotlin.presentation.backgroundtext


import com.imr.example.newsmartykotlin.presentation.backgroundtext.components.TextEditingTab
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.EditableStickerItem

data class BackgroundTextUiState(
    val imagePath: String = "",
    val backgroundPath: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val stickers: List<EditableStickerItem> = emptyList(),
    val selectedStickerId: Long? = null,
    val showStickerSheet: Boolean = false,
    val showAddTextDialog: Boolean = false,
    val inputText: String = "",
    val selectedTextEditingTab: TextEditingTab = TextEditingTab.Fonts
) {
    val selectedSticker: EditableStickerItem?
        get() = stickers.firstOrNull { it.id == selectedStickerId }

    val selectedTextSticker: EditableStickerItem?
        get() = selectedSticker?.takeIf { it.isText && it.isSelected }
}
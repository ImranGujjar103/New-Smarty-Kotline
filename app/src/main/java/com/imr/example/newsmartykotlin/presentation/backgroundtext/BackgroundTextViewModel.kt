package com.imr.example.newsmartykotlin.presentation.backgroundtext

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.backgroundtext.SaveBackgroundTextImageUseCase
import com.imr.example.newsmartykotlin.presentation.backgroundtext.components.TextEditingTab
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.EditableStickerItem
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.StickerType
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.TextFontOption
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackgroundTextViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveBackgroundTextImageUseCase: SaveBackgroundTextImageUseCase
) : ViewModel() {

    private val imagePath: String =
        savedStateHandle[AppRoutes.BackgroundText.ARG_IMAGE_PATH] ?: ""

    private val _uiState = MutableStateFlow(
        BackgroundTextUiState(imagePath = imagePath)
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<BackgroundTextEvent>()
    val event = _event.asSharedFlow()

    fun onInputTextChange(value: String) {
        _uiState.update { it.copy(inputText = value) }
    }

    fun showAddTextDialog() {
        unselectAll()
        _uiState.update {
            it.copy(
                showAddTextDialog = true,
                showStickerSheet = false
            )
        }
    }

    fun hideAddTextDialog() {
        _uiState.update {
            it.copy(
                showAddTextDialog = false,
                inputText = ""
            )
        }
    }

    fun showStickerSheet() {
        unselectAll()
        _uiState.update {
            it.copy(
                showStickerSheet = true,
                showAddTextDialog = false
            )
        }
    }

    fun hideStickerSheet() {
        _uiState.update {
            it.copy(showStickerSheet = false)
        }
    }
    fun updateBackground(backgroundUrl: String) {
        _uiState.value = _uiState.value.copy(
            backgroundPath = backgroundUrl
        )
    }
    fun addEmojiSticker(value: String) {
        val id = System.currentTimeMillis()

        _uiState.update { state ->
            state.copy(
                stickers = state.stickers
                    .map { it.copy(isSelected = false) }
                    .plus(
                        EditableStickerItem(
                            id = id,
                            value = value,
                            type = StickerType.Emoji,
                            isSelected = true
                        )
                    ),
                selectedStickerId = id,
                showStickerSheet = false
            )
        }
    }

    fun addTextSticker() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) {
            hideAddTextDialog()
            return
        }

        val id = System.currentTimeMillis()

        _uiState.update { state ->
            state.copy(
                stickers = state.stickers
                    .map { it.copy(isSelected = false) }
                    .plus(
                        EditableStickerItem(
                            id = id,
                            value = text,
                            type = StickerType.Text,
                            isSelected = true
                        )
                    ),
                selectedStickerId = id,
                inputText = "",
                showAddTextDialog = false
            )
        }
    }

    fun selectSticker(id: Long) {
        _uiState.update { state ->
            val selected = state.stickers.firstOrNull { it.id == id }

            state.copy(
                stickers = state.stickers.map {
                    it.copy(isSelected = it.id == id)
                },
                selectedStickerId = id,
                showStickerSheet = false,
                showAddTextDialog = false
            )
        }
    }

    fun unselectAll() {
        _uiState.update { state ->
            state.copy(
                stickers = state.stickers.map {
                    it.copy(isSelected = false)
                },
                selectedStickerId = null
            )
        }
    }

    fun deleteSticker(id: Long) {
        _uiState.update { state ->
            state.copy(
                stickers = state.stickers.filterNot { it.id == id },
                selectedStickerId = null
            )
        }
    }

    fun updateStickerTransform(
        id: Long,
        pan: Offset = Offset.Zero,
        zoom: Float = 1f,
        rotation: Float = 0f
    ) {
        _uiState.update { state ->
            state.copy(
                stickers = state.stickers.map { sticker ->
                    if (sticker.id == id && sticker.isSelected) {
                        sticker.copy(
                            offset = sticker.offset + pan,
                            scale = (sticker.scale * zoom).coerceIn(0.2f, 4f),
                            rotation = sticker.rotation + rotation
                        )
                    } else {
                        sticker
                    }
                }
            )
        }
    }

    fun updateStickerScaleByDelta(id: Long, delta: Float) {
        _uiState.update { state ->
            state.copy(
                stickers = state.stickers.map { sticker ->
                    if (sticker.id == id && sticker.isSelected) {
                        sticker.copy(
                            scale = (sticker.scale + delta).coerceIn(0.2f, 4f)
                        )
                    } else {
                        sticker
                    }
                }
            )
        }
    }

    fun updateStickerRotationByDelta(id: Long, delta: Float) {
        _uiState.update { state ->
            state.copy(
                stickers = state.stickers.map { sticker ->
                    if (sticker.id == id && sticker.isSelected) {
                        sticker.copy(rotation = sticker.rotation + delta)
                    } else {
                        sticker
                    }
                }
            )
        }
    }

    fun updateSelectedTextValue(value: String) {
        updateSelectedTextSticker {
            it.copy(value = value)
        }
    }

    fun updateSelectedTextColor(color: Color) {
        updateSelectedTextSticker {
            it.copy(textStyle = it.textStyle.copy(color = color))
        }
    }

    fun updateSelectedFont(font: TextFontOption) {
        updateSelectedTextSticker {
            it.copy(
                textStyle = it.textStyle.copy(
                    fontId = font.id
                )
            )
        }
    }

    fun updateSelectedAlignment(textAlign: TextAlign) {
        updateSelectedTextSticker {
            it.copy(textStyle = it.textStyle.copy(textAlign = textAlign))
        }
    }

    fun updateSelectedShadow(enabled: Boolean) {
        updateSelectedTextSticker {
            it.copy(textStyle = it.textStyle.copy(shadowEnabled = enabled))
        }
    }

    fun updateSelectedFontSize(size: Float) {
        updateSelectedTextSticker {
            it.copy(textStyle = it.textStyle.copy(fontSize = size.coerceIn(18f, 90f)))
        }
    }

    fun updateTextEditingTab(tab: TextEditingTab) {
        _uiState.update {
            it.copy(selectedTextEditingTab = tab)
        }
    }

    private fun updateSelectedTextSticker(
        update: (EditableStickerItem) -> EditableStickerItem
    ) {
        val selectedId = _uiState.value.selectedStickerId ?: return

        _uiState.update { state ->
            state.copy(
                stickers = state.stickers.map { sticker ->
                    if (sticker.id == selectedId && sticker.isText) {
                        update(sticker)
                    } else {
                        sticker
                    }
                }
            )
        }
    }

    fun onDoneClick(bitmap: Bitmap) {
        unselectAll()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            runCatching {
                saveBackgroundTextImageUseCase(bitmap)
            }.onSuccess { path ->
                _uiState.update { it.copy(isSaving = false) }
                _event.emit(BackgroundTextEvent.Done(path))
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }
}
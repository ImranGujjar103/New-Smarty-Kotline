package com.imr.example.newsmartykotlin.presentation.bgremovereditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.bgremovereditor.ExportBgRemoverImageUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BgRemoverEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val exportBgRemoverImageUseCase: ExportBgRemoverImageUseCase
) : ViewModel() {

    private val removedImageUri: String =
        savedStateHandle.get<String>(
            AppRoutes.BgRemoverEditor.ARG_REMOVED_IMAGE_URI
        ).orEmpty()

    private val _uiState = MutableStateFlow(
        BgRemoverEditorUiState(
            removedImageUri = removedImageUri
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<BgRemoverEditorEvent>()
    val event = _event.asSharedFlow()

    fun onAction(action: BgRemoverEditorAction) {
        when (action) {
            BgRemoverEditorAction.BackClick -> {
                viewModelScope.launch {
                    _event.emit(BgRemoverEditorEvent.Back)
                }
            }

            BgRemoverEditorAction.SaveClick -> saveImage()

            is BgRemoverEditorAction.TabClick -> {
                _uiState.value = _uiState.value.copy(
                    selectedTab = action.tab
                )
            }

            is BgRemoverEditorAction.BackgroundClick -> {
                _uiState.value = _uiState.value.copy(
                    selectedBackground = action.background
                )
            }
        }
    }

    fun updateBackground(backgroundUrl: String) {
        _uiState.value = _uiState.value.copy(
            selectedBackground = BgEditorBackground.GalleryImage(
                imageUri = backgroundUrl
            )
        )
    }

    private fun saveImage() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val imagePath = exportBgRemoverImageUseCase(
                    removedImageUri = _uiState.value.removedImageUri,
                    background = _uiState.value.selectedBackground
                )

                _uiState.value = _uiState.value.copy(isSaving = false)
                _event.emit(BgRemoverEditorEvent.Saved(imagePath))

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                _event.emit(
                    BgRemoverEditorEvent.Error(
                        e.message ?: "Unable to save image"
                    )
                )
            }
        }
    }
}
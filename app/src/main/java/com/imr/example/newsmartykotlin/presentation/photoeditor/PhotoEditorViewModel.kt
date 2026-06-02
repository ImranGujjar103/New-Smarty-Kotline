package com.imr.example.newsmartykotlin.presentation.photoeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.photoeditor.GetPhotoEditorSuitUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPhotoEditorSuitUseCase: GetPhotoEditorSuitUseCase
) : ViewModel() {

    private val suitId: String =
        savedStateHandle[AppRoutes.PhotoEditor.ARG_SUIT_ID] ?: ""

    private val faceImageUri: String =
        savedStateHandle[AppRoutes.PhotoEditor.ARG_CROPPED_IMAGE_URI] ?: ""

    private val _uiState = MutableStateFlow(
        PhotoEditorUiState(
            suitId = suitId,
            faceImageUri = faceImageUri
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadSuit()
    }

    private fun loadSuit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val suit = getPhotoEditorSuitUseCase(suitId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    suitItem = suit,
                    errorMessage = if (suit == null) "Suit not found" else null
                )
            }
        }
    }
}
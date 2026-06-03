package com.imr.example.newsmartykotlin.presentation.photoeditor

import android.util.Log
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

    private val suitUrl: String =
        savedStateHandle[AppRoutes.PhotoEditor.ARG_SUIT_URL] ?: ""

    private val faceImageUri: String =
        savedStateHandle[AppRoutes.PhotoEditor.ARG_CROPPED_IMAGE_URI] ?: ""

    private val _uiState = MutableStateFlow(
        PhotoEditorUiState(
            suitUrl = suitUrl,
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

            Log.d("PhotoEditorVM", "suitUrl = $suitUrl")

            Log.d("PhotoEditorVM", "loaded suit = $suitUrl")
            _uiState.update {
                it.copy(
                    isLoading = false,
                    suitUrl = suitUrl,
                    errorMessage = null
                )
            }

            Log.d("PhotoEditorVM", "suitUrl = $suitUrl")
            Log.d("PhotoEditorVM", "faceImageUri = $faceImageUri")
        }
    }


    fun onEraserDone(newImageUri: String) {
        _uiState.update {
            it.copy(
                mergedImageUri = newImageUri,
                faceImageUri = newImageUri,
                suitUrl = ""
            )
        }
    }

    fun onSuitChanged(newSuitUrl: String) {
        _uiState.update {
            it.copy(
                suitUrl = newSuitUrl
            )
        }
    }
}
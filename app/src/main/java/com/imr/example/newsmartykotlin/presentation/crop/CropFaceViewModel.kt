package com.imr.example.newsmartykotlin.presentation.crop

import android.graphics.RectF
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.model.CropAspectRatio
import com.imr.example.newsmartykotlin.domain.usecase.crop.CropImageUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CropFaceViewModel(
    savedStateHandle: SavedStateHandle,
    private val cropImageUseCase: CropImageUseCase
) : ViewModel() {

    private val suitId: String =
        savedStateHandle[AppRoutes.CropFace.ARG_SUIT_URL] ?: ""

    private val imageUri: String =
        savedStateHandle[AppRoutes.CropFace.ARG_IMAGE_URI] ?: ""

    private val _uiState = MutableStateFlow(
        CropFaceUiState(
            suitId = suitId,
            imageUri = imageUri
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CropFaceEvent>()
    val event: SharedFlow<CropFaceEvent> = _event.asSharedFlow()

    fun onBackClick() {
        viewModelScope.launch {
            _event.emit(CropFaceEvent.NavigateBack)
        }
    }

    fun onRatioSelected(ratio: CropAspectRatio) {
        _uiState.update {
            it.copy(selectedRatio = ratio)
        }
    }

    fun onCropAreaChanged(
        imageBounds: RectF,
        cropRect: RectF
    ) {
        _uiState.update {
            it.copy(
                imageBounds = imageBounds,
                cropRect = cropRect
            )
        }
    }

    fun onNextClick() {
        val state = _uiState.value
        val imageBounds = state.imageBounds
        val cropRect = state.cropRect

        if (imageBounds == null || cropRect == null) {
            _uiState.update {
                it.copy(errorMessage = "Failed to crop image")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCropping = true,
                    errorMessage = null
                )
            }

            try {
                val croppedUri = cropImageUseCase(
                    sourceUri = state.imageUri,
                    imageBounds = imageBounds,
                    cropRect = cropRect
                )

                _uiState.update {
                    it.copy(
                        isCropping = false,
                        croppedImageUri = croppedUri
                    )
                }

                _event.emit(
                    CropFaceEvent.NavigateNext(
                        suitId = state.suitId,
                        croppedImageUri = croppedUri
                    )
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCropping = false,
                        errorMessage = e.message ?: "Failed to crop image"
                    )
                }
            }
        }
    }
}
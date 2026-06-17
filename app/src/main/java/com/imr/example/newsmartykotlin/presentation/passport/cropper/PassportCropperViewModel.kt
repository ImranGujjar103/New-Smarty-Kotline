package com.imr.example.newsmartykotlin.presentation.passport.cropper

import android.graphics.RectF
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.getPixel
import com.imr.example.newsmartykotlin.domain.model.getSizeInch
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository
import com.imr.example.newsmartykotlin.domain.usecase.passport.CropPassportImageUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PassportCropperViewModel(
    savedStateHandle: SavedStateHandle,
    private val passportRepository: PassportRepository,
    private val cropPassportImageUseCase: CropPassportImageUseCase
) : ViewModel() {

    private val imageUri: String =
        savedStateHandle[AppRoutes.PassportCropper.ARG_IMAGE_URI] ?: ""

    private val countryId: String =
        savedStateHandle[AppRoutes.PassportCropper.ARG_COUNTRY_ID] ?: ""

    private val documentType: String =
        savedStateHandle[AppRoutes.PassportCropper.ARG_DOCUMENT_TYPE] ?: DocumentType.PASSPORT.name

    private val selectedType: DocumentType =
        runCatching { DocumentType.valueOf(documentType) }.getOrDefault(DocumentType.PASSPORT)

    private val country = passportRepository.getCountryById(countryId)

    private val _uiState = MutableStateFlow(
        PassportCropperUiState(
            imageUri = imageUri,
            countryId = countryId,
            documentType = documentType,
            pixelText = country?.getPixel(selectedType).orEmpty(),
            inchText = country?.getSizeInch(selectedType).orEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PassportCropperEvent>()
    val event = _event.asSharedFlow()

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

    fun onBackClick() {
        viewModelScope.launch {
            _event.emit(PassportCropperEvent.NavigateBack)
        }
    }

    fun onContinueClick() {
        val state = _uiState.value
        val imageBounds = state.imageBounds
        val cropRect = state.cropRect

        if (imageBounds == null || cropRect == null) {
            _uiState.update {
                it.copy(errorMessage = "Please adjust image")
            }
            return
        }

        val width = state.pixelText.substringBefore("x").trim().toIntOrNull() ?: 600
        val height = state.pixelText.substringAfter("x").substringBefore("px").trim().toIntOrNull() ?: 600

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(isCropping = true, errorMessage = null)
                }

                val croppedUri = cropPassportImageUseCase(
                    sourceUri = state.imageUri,
                    imageBounds = imageBounds,
                    cropRect = cropRect,
                    outputWidth = width,
                    outputHeight = height
                )

                _uiState.update {
                    it.copy(isCropping = false)
                }

                _event.emit(
                    PassportCropperEvent.NavigateToBackgroundRemove(
                        croppedImageUri = croppedUri,
                        countryId = state.countryId,
                        documentType = state.documentType
                    )
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCropping = false,
                        errorMessage = e.message ?: "Crop failed"
                    )
                }
            }
        }
    }
}
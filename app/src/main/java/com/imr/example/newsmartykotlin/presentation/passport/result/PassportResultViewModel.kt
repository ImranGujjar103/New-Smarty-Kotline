package com.imr.example.newsmartykotlin.presentation.passport.result

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.core.utils.ImageSaver
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.getPixel
import com.imr.example.newsmartykotlin.domain.model.getSizeInch
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PassportResultViewModel(
    savedStateHandle: SavedStateHandle,
    private val context: Context,
    private val passportRepository: PassportRepository
) : ViewModel() {

    private val imageUri: String =
        savedStateHandle[AppRoutes.PassportResult.ARG_IMAGE_URI] ?: ""

    private val countryId: String =
        savedStateHandle[AppRoutes.PassportResult.ARG_COUNTRY_ID] ?: ""

    private val documentType: String =
        savedStateHandle[AppRoutes.PassportResult.ARG_DOCUMENT_TYPE] ?: DocumentType.PASSPORT.name

    private val selectedType: DocumentType =
        runCatching { DocumentType.valueOf(documentType) }.getOrDefault(DocumentType.PASSPORT)

    private val country = passportRepository.getCountryById(countryId)

    private val _uiState = MutableStateFlow(
        PassportResultUiState(
            imageUri = imageUri,
            countryId = countryId,
            documentType = documentType,
            pixelText = country?.getPixel(selectedType).orEmpty(),
            inchText = country?.getSizeInch(selectedType).orEmpty(),
            fileSizeText = getFileSizeText(imageUri)
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PassportResultEvent>()
    val event = _event.asSharedFlow()

    fun onBackClick() {
        viewModelScope.launch {
            _event.emit(PassportResultEvent.NavigateBack)
        }
    }

    fun onTryAgainClick() {
        viewModelScope.launch {
            _event.emit(PassportResultEvent.TryAgain)
        }
    }
    fun onBackgroundClick() {
        viewModelScope.launch {
            _event.emit(
                PassportResultEvent.NavigateToBackground(
                    _uiState.value.imageUri
                )
            )
        }
    }

    fun onBackgroundImageUpdated(updatedImageUri: String) {
        _uiState.value = _uiState.value.copy(
            imageUri = updatedImageUri,
            fileSizeText = getFileSizeText(updatedImageUri)
        )
    }

    fun onSaveClick() {
        val savedUri = ImageSaver.saveImageToPictures(
            context = context,
            sourceUri = _uiState.value.imageUri
        )

        viewModelScope.launch {
            savedUri?.let {
                _event.emit(
                    PassportResultEvent.ImageSaved(
                        uri = it.toString()
                    )
                )
            }
        }
    }

    @SuppressLint("Recycle")
    private fun getFileSizeText(uri: String): String {
        return runCatching {
            val size = context.contentResolver
                .openAssetFileDescriptor(uri.toUri(), "r")
                ?.length ?: 0L

            if (size <= 0L) {
                ""
            } else {
                val kb = size / 1024
                "$kb KB"
            }
        }.getOrDefault("")
    }
}
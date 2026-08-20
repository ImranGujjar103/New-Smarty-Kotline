package com.imr.example.newsmartykotlin.presentation.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.model.GalleryImage
import com.imr.example.newsmartykotlin.domain.usecase.gallery.GetGalleryImagesUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleryViewModel(
    savedStateHandle: SavedStateHandle,
    private val getGalleryImagesUseCase: GetGalleryImagesUseCase
) : ViewModel() {

    private val isForBgRemover: Boolean =
        savedStateHandle[AppRoutes.GalleryForBgRemover.ARG_IS_BG_REMOVER] ?: false

    private val _uiState = MutableStateFlow(
        GalleryUiState(
            isForBgRemover = isForBgRemover
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<GalleryEvent>()
    val event: SharedFlow<GalleryEvent> = _event.asSharedFlow()

    private val suitId: String =
        savedStateHandle[AppRoutes.Gallery.ARG_SUIT_URL] ?: ""

    private val isForPassport: Boolean =
        savedStateHandle[AppRoutes.GalleryForPassport.ARG_IS_FOR_PASSPORT] ?: false

    private val countryId: String =
        savedStateHandle[AppRoutes.GalleryForPassport.ARG_COUNTRY_ID] ?: ""

    private val documentType: String =
        savedStateHandle[AppRoutes.GalleryForPassport.ARG_DOCUMENT_TYPE] ?: ""

    fun loadGalleryImages() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                val images = getGalleryImagesUseCase()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        images = images,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load images"
                    )
                }
            }
        }
    }

    fun updatePermissionStatus(isLimited: Boolean) {
        _uiState.update { it.copy(isLimitedAccess = isLimited) }
    }

    fun onFolderClick(folderName: String) {
        _uiState.update {
            it.copy(selectedFolderName = folderName)
        }
    }

    fun onImageClick(image: GalleryImage) {
        viewModelScope.launch {
            when {
                isForPassport -> {
                    _event.emit(
                        GalleryEvent.NavigateToPassportCrop(
                            imageUri = image.uri,
                            countryId = countryId,
                            documentType = documentType
                        )
                    )
                }

                isForBgRemover -> {
                    _event.emit(
                        GalleryEvent.NavigateToBgRemoverCrop(
                            imageUri = image.uri
                        )
                    )
                }

                else -> {
                    _event.emit(
                        GalleryEvent.NavigateToCropFace(
                            suitUrl = suitId,
                            imageUri = image.uri
                        )
                    )
                }
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _event.emit(GalleryEvent.NavigateBack)
        }
    }
}
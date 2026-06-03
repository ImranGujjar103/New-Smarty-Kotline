package com.imr.example.newsmartykotlin.presentation.gallery

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<GalleryEvent>()
    val event: SharedFlow<GalleryEvent> = _event.asSharedFlow()
    val suitId: String =
        savedStateHandle[AppRoutes.Gallery.ARG_SUIT_URL] ?: ""
    fun loadGalleryImages() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
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

        Log.d("GallerySuitItem", "Suit Id 222= $suitId")

    }
    fun onFolderClick(folderName: String) {
        _uiState.update {
            it.copy(selectedFolderName = folderName)
        }
    }
    fun onBackClick() {
        viewModelScope.launch {
            _event.emit(GalleryEvent.NavigateBack)
        }
    }
}
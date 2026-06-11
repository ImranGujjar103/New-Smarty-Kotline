package com.imr.example.newsmartykotlin.presentation.bgremove

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.bgremove.RemoveBackgroundUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BgRemoveViewModel(
    savedStateHandle: SavedStateHandle,
    private val removeBackgroundUseCase: RemoveBackgroundUseCase
) : ViewModel() {

    private val isForBgRemover: Boolean =
        savedStateHandle[AppRoutes.BgRemoveForBgRemover.ARG_IS_BG_REMOVER] ?: false

    private val suitId: String =
        savedStateHandle[AppRoutes.BgRemove.ARG_SUIT_URL] ?: ""

    private val croppedImageUri: String =
        if (isForBgRemover) {
            savedStateHandle[AppRoutes.BgRemoveForBgRemover.ARG_CROPPED_IMAGE_URI] ?: ""
        } else {
            savedStateHandle[AppRoutes.BgRemove.ARG_CROPPED_IMAGE_URI] ?: ""
        }

    private val _uiState = MutableStateFlow(
        BgRemoveUiState(
            suitId = suitId,
            croppedImageUri = croppedImageUri
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<BgRemoveEvent>()
    val event = _event.asSharedFlow()

    init {
        removeBackground()
    }

    private fun removeBackground() {
        viewModelScope.launch {
            try {
                startFakeProgress()

                val resultUri = removeBackgroundUseCase(croppedImageUri)

                _uiState.update {
                    it.copy(
                        progress = 1f,
                        progressText = "100%",
                        isLoading = false,
                        removedBgImageUri = resultUri
                    )
                }

                delay(300)

                if (isForBgRemover) {
                    _event.emit(
                        BgRemoveEvent.NavigateToBgRemoverEditor(
                            removedBgImageUri = resultUri
                        )
                    )
                } else {
                    _event.emit(
                        BgRemoveEvent.NavigateToSuitEditor(
                            suitId = suitId,
                            removedBgImageUri = resultUri
                        )
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Background remove failed"
                    )
                }
            }
        }
    }

    private suspend fun startFakeProgress() {
        for (progress in 50..90 step 5) {
            delay(180)
            _uiState.update {
                it.copy(
                    progress = progress / 100f,
                    progressText = "$progress%"
                )
            }
        }
    }
}
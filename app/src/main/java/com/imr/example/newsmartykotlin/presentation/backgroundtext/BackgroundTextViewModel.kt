package com.imr.example.newsmartykotlin.presentation.backgroundtext

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.backgroundtext.SaveBackgroundTextImageUseCase
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
        BackgroundTextUiState(
            imagePath = imagePath
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<BackgroundTextEvent>()
    val event = _event.asSharedFlow()

    fun onDoneClick(bitmap: Bitmap) {
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
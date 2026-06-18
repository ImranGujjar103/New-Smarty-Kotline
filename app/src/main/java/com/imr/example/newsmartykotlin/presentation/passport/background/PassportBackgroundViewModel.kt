package com.imr.example.newsmartykotlin.presentation.passport.background

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PassportBackgroundViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val imageUri: String =
        savedStateHandle[AppRoutes.Background.ARG_IMAGE_URI] ?: ""

    private val _uiState = MutableStateFlow(
        PassportBackgroundUiState(
            imageUri = imageUri
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PassportBackgroundEvent>()
    val event = _event.asSharedFlow()

    fun onBackClick() {
        viewModelScope.launch {
            _event.emit(PassportBackgroundEvent.NavigateBack)
        }
    }

    fun onDoneClick() {
        viewModelScope.launch {
            _event.emit(PassportBackgroundEvent.CaptureAndDone)
        }
    }

    fun onColorSelected(color: Color) {
        _uiState.update {
            it.copy(selectedColor = color)
        }
    }

    fun onBottomSheetToggle() {
        _uiState.update {
            it.copy(isBottomSheetExpanded = !it.isBottomSheetExpanded)
        }
    }
}
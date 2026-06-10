package com.imr.example.newsmartykotlin.presentation.saved

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SavedViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val imagePath: String =
        savedStateHandle[AppRoutes.Saved.ARG_IMAGE_PATH] ?: ""

    private val _uiState = MutableStateFlow(
        SavedUiState(imagePath = imagePath)
    )
    val uiState: StateFlow<SavedUiState> = _uiState

    private val _event = MutableSharedFlow<SavedEvent>()
    val event: SharedFlow<SavedEvent> = _event.asSharedFlow()

    fun onTryMoreClick() {
        viewModelScope.launch {
            _event.emit(SavedEvent.NavigateTryMore)
        }
    }

    fun onShareClick() {
        viewModelScope.launch {
            _event.emit(SavedEvent.ShareImage(imagePath))
        }
    }

    fun onInstagramClick() {
        viewModelScope.launch {
            _event.emit(SavedEvent.ShareImage(imagePath))
        }
    }

    fun onFacebookClick() {
        viewModelScope.launch {
            _event.emit(SavedEvent.ShareImage(imagePath))
        }
    }

    fun onWhatsAppClick() {
        viewModelScope.launch {
            _event.emit(SavedEvent.ShareImage(imagePath))
        }
    }

    fun onXClick() {
        viewModelScope.launch {
            _event.emit(SavedEvent.ShareImage(imagePath))
        }
    }
}
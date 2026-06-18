package com.imr.example.newsmartykotlin.presentation.saved

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.model.DocumentType
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

    private val isForPassport: Boolean =
        savedStateHandle[AppRoutes.Saved.ARG_IS_FOR_PASSPORT] ?: false

    private val countryId: String =
        savedStateHandle[AppRoutes.Saved.ARG_COUNTRY_ID] ?: ""

    private val documentType: String =
        savedStateHandle[AppRoutes.Saved.ARG_DOCUMENT_TYPE] ?: DocumentType.PASSPORT.name

    private val _uiState = MutableStateFlow(
        SavedUiState(
            imagePath = imagePath,
            isForPassport = isForPassport,
            countryId = countryId,
            documentType = documentType
        )
    )

    val uiState: StateFlow<SavedUiState> = _uiState

    private val _event = MutableSharedFlow<SavedEvent>()
    val event: SharedFlow<SavedEvent> = _event.asSharedFlow()

    fun onTryMoreClick() {
        viewModelScope.launch {
            _event.emit(
                if (isForPassport) {
                    SavedEvent.NavigatePassportTryMore(
                        countryId = countryId,
                        documentType = documentType
                    )
                } else {
                    SavedEvent.NavigateTryMore
                }
            )
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
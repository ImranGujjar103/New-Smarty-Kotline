package com.imr.example.newsmartykotlin.presentation.permission

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.gallery.GetPermissionDenyCountUseCase
import com.imr.example.newsmartykotlin.domain.usecase.gallery.IncrementPermissionDenyCountUseCase
import com.imr.example.newsmartykotlin.domain.usecase.gallery.ResetPermissionDenyCountUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleryPermissionViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPermissionDenyCountUseCase: GetPermissionDenyCountUseCase,
    private val incrementPermissionDenyCountUseCase: IncrementPermissionDenyCountUseCase,
    private val resetPermissionDenyCountUseCase: ResetPermissionDenyCountUseCase
) : ViewModel() {

    private val tag = "GalleryPermissionVM"

    private val _uiState = MutableStateFlow(GalleryPermissionUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<GalleryPermissionEvent>()
    val event: SharedFlow<GalleryPermissionEvent> = _event.asSharedFlow()

    val suitId: String =
        savedStateHandle[AppRoutes.GalleryPermission.ARG_SUIT_URL] ?: ""

    private val isForPassport: Boolean =
        savedStateHandle[AppRoutes.GalleryPermissionForPassport.ARG_IS_FOR_PASSPORT] ?: false

    private val isForBgRemover: Boolean =
        savedStateHandle[AppRoutes.GalleryPermissionForBgRemover.ARG_IS_BG_REMOVER] ?: false

    private val isForBackground: Boolean =
        savedStateHandle[AppRoutes.GalleryPermissionForBackground.ARG_IS_FOR_BACKGROUND] ?: false

    private val countryId: String =
        savedStateHandle[AppRoutes.GalleryPermissionForPassport.ARG_COUNTRY_ID] ?: ""

    private val documentType: String =
        savedStateHandle[AppRoutes.GalleryPermissionForPassport.ARG_DOCUMENT_TYPE] ?: ""

    init {
        observeDenyCount()
    }

    private fun observeDenyCount() {
        viewModelScope.launch {
            getPermissionDenyCountUseCase().collect { count ->
                Log.d(tag, "observeDenyCount: $count")
                _uiState.update {
                    it.copy(denyCount = count)
                }
            }
        }

        Log.d(tag, "Suit Id = $suitId")
        Log.d(tag, "isForPassport = $isForPassport")
        Log.d(tag, "isForBgRemover = $isForBgRemover")
        Log.d(tag, "isForBackground = $isForBackground")
        Log.d(tag, "countryId = $countryId")
        Log.d(tag, "documentType = $documentType")
    }

    fun onGrantPermissionClick() {
        val denyCount = _uiState.value.denyCount

        if (denyCount >= 2) {
            _uiState.update {
                it.copy(
                    showUnlockDialog = false,
                    showSettingsDialog = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    showUnlockDialog = true,
                    showSettingsDialog = false
                )
            }
        }
    }

    fun onUnlockDialogCancel() {
        _uiState.update {
            it.copy(showUnlockDialog = false)
        }
    }

    fun onUnlockDialogAllow() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(showUnlockDialog = false)
            }

            _event.emit(GalleryPermissionEvent.RequestPermission)
        }
    }

    fun onPermissionGranted() {
        if (_uiState.value.isPermissionGranted) return

        _uiState.update {
            it.copy(
                isPermissionGranted = true,
                showSettingsDialog = false,
                showUnlockDialog = false
            )
        }

        viewModelScope.launch {
            resetPermissionDenyCountUseCase()

            if (isForPassport) {
                _event.emit(
                    GalleryPermissionEvent.NavigatePassportGallery(
                        countryId = countryId,
                        documentType = documentType
                    )
                )
            } else if (isForBgRemover) {
                _event.emit(GalleryPermissionEvent.NavigateBgRemoverGallery)
            } else if (isForBackground) {
                _event.emit(GalleryPermissionEvent.NavigateBackgroundGallery)
            } else {
                _event.emit(GalleryPermissionEvent.NavigateGallery)
            }
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            incrementPermissionDenyCountUseCase()

            _uiState.update {
                it.copy(
                    isPermissionGranted = false,
                    showUnlockDialog = false
                )
            }
        }
    }

    fun showSettingsDialog() {
        _uiState.update {
            it.copy(
                showSettingsDialog = true,
                showUnlockDialog = false
            )
        }
    }

    fun onSettingsDialogCancel() {
        _uiState.update {
            it.copy(showSettingsDialog = false)
        }
    }

    fun onSettingsClick() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(showSettingsDialog = false)
            }

            _event.emit(GalleryPermissionEvent.OpenSettings)
        }
    }

    fun checkPermissionOnResume(isGranted: Boolean) {
        if (isGranted) {
            onPermissionGranted()
        } else {
            _uiState.update {
                it.copy(isPermissionGranted = false)
            }
        }
    }
}
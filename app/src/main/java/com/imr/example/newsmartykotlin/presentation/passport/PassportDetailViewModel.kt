package com.imr.example.newsmartykotlin.presentation.passport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository
import androidx.lifecycle.SavedStateHandle
import com.imr.example.newsmartykotlin.core.permission.AppPermissionType
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PassportDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PassportRepository,
    private val dataStorePrefs: DataStorePrefs
) : ViewModel() {

    private val finalImageUri: String? =
        savedStateHandle[AppRoutes.PassportDetail.ARG_FINAL_IMAGE_URI]

    private val _uiState = MutableStateFlow(
        PassportDetailUiState(
            finalImageUri = finalImageUri?.takeIf { it.isNotBlank() }
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        observeCameraDenyCount()
    }

    private fun observeCameraDenyCount() {
        viewModelScope.launch {
            dataStorePrefs.getPermissionDeniedCount(AppPermissionType.CAMERA).collect { count ->
                _uiState.update { it.copy(cameraDenyCount = count) }
            }
        }
    }

    fun getCountry(id: String): PassportCountry? {
        return repository.getCountryById(id)
    }

    fun onCameraClick() {
        if (_uiState.value.cameraDenyCount >= 2) {
            _uiState.update {
                it.copy(showSettingsDialog = true)
            }
        } else {
            _uiState.update {
                it.copy(showPermissionDialog = true)
            }
        }
    }

    fun onPermissionDialogAllow() {
        _uiState.update { it.copy(showPermissionDialog = false) }
    }

    fun onPermissionDialogDeny() {
        _uiState.update { it.copy(showPermissionDialog = false) }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            dataStorePrefs.increasePermissionDeniedCount(AppPermissionType.CAMERA)
        }
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            dataStorePrefs.resetPermissionDeniedCount(AppPermissionType.CAMERA)
        }
    }

    fun onSettingsDialogCancel() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun onSettingsClick() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun showSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun checkPermissionOnResume(isGranted: Boolean) {
        if (isGranted) {
            onPermissionGranted()
            _uiState.update {
                it.copy(showSettingsDialog = false)
            }
        }
    }
}
package com.imr.example.newsmartykotlin.presentation.passport

import androidx.lifecycle.ViewModel
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository
import androidx.lifecycle.SavedStateHandle
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PassportDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PassportRepository
) : ViewModel() {

    private val finalImageUri: String? =
        savedStateHandle[AppRoutes.PassportDetail.ARG_FINAL_IMAGE_URI]

    private val _uiState = MutableStateFlow(
        PassportDetailUiState(
            finalImageUri = finalImageUri?.takeIf { it.isNotBlank() }
        )
    )
    val uiState = _uiState.asStateFlow()

    fun getCountry(id: String): PassportCountry? {
        return repository.getCountryById(id)
    }
}
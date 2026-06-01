package com.imr.example.newsmartykotlin.presentation.home

import androidx.lifecycle.ViewModel
import com.imr.example.newsmartykotlin.domain.usecase.GetHomeFeaturesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    getHomeFeaturesUseCase: GetHomeFeaturesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            features = getHomeFeaturesUseCase()
        )
    )

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
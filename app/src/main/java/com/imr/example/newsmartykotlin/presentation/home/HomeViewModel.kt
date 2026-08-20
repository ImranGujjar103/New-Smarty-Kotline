package com.imr.example.newsmartykotlin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.domain.usecase.home.GetHomeFeaturesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    getHomeFeaturesUseCase: GetHomeFeaturesUseCase,
    val dataStorePrefs: DataStorePrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            features = getHomeFeaturesUseCase()
        )
    )

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val isAppRated = dataStorePrefs.getIsAppRated()

    fun isFirstSplash(isFirstSplash : Boolean){
        viewModelScope.launch {
            dataStorePrefs.isFirstSplash(isFirstSplash)
        }
    }

    fun setHomeInterstitialFirstTime(isFirstTime: Boolean) {
        viewModelScope.launch {
            dataStorePrefs.isHomeInterstitialFirstTime(isFirstTime)
        }
    }

    fun setAppRated(isRated: Boolean) {
        viewModelScope.launch {
            dataStorePrefs.setIsAppRated(isRated)
        }
    }
}

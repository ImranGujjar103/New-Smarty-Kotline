package com.imr.example.newsmartykotlin.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.domain.model.LanguageModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStorePrefs: DataStorePrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<SettingsEvent>()
    val event = _event.asSharedFlow()

    init {
        observeData()
    }

    private fun observeData() {
        dataStorePrefs.getSelectedLanguageData()
            .onEach { data ->
                if (data.isNotEmpty()) {
                    runCatching {
                        val language = Gson().fromJson(data, LanguageModel::class.java)
                        _uiState.update { it.copy(selectedLanguageName = language.languageName) }
                    }
                }
            }
            .launchIn(viewModelScope)

        dataStorePrefs.getIsPurchased()
            .onEach { isPurchased ->
                _uiState.update { it.copy(isPremium = isPurchased) }
            }
            .launchIn(viewModelScope)
    }

    fun onBackClick() {
        viewModelScope.launch { _event.emit(SettingsEvent.NavigateBack) }
    }

    fun onLanguageClick() {
        viewModelScope.launch { _event.emit(SettingsEvent.NavigateToLanguage) }
    }

    fun onPremiumClick() {
        viewModelScope.launch { _event.emit(SettingsEvent.NavigateToPremium) }
    }

    fun onPrivacyClick() {
        viewModelScope.launch { _event.emit(SettingsEvent.PrivacyPolicy) }
    }

    fun onRateClick() {
        viewModelScope.launch { _event.emit(SettingsEvent.RateApp) }
    }

    fun onShareClick() {
        viewModelScope.launch { _event.emit(SettingsEvent.ShareApp) }
    }
}

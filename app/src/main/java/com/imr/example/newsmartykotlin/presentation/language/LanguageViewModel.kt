package com.imr.example.newsmartykotlin.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.imr.example.newsmartykotlin.core.extensions.getLocalizationList
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.domain.model.LanguageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val dataStorePrefs: DataStorePrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState

    private var tempSelectedLanguage: LanguageModel? = null

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            val selectedCode = dataStorePrefs.getSelectedLanguageCode().first()
            val isLanguageSelected = dataStorePrefs.getLanguageSelected().first()
            val handShown = dataStorePrefs.getLanguageHandShown().first()

            val list = getLocalizationList()

            val updatedList = if (selectedCode.isNotEmpty()) {
                list.map {
                    it.copy(isSelected = it.languageCode == selectedCode)
                }.sortedByDescending { it.isSelected }
            } else {
                list
            }

            tempSelectedLanguage = updatedList.firstOrNull { it.isSelected }

            _uiState.value = LanguageUiState(
                languages = updatedList,
                selectedLanguage = tempSelectedLanguage,
                isSaveEnabled = isLanguageSelected,
                showHandAnimation = !handShown && !isLanguageSelected
            )
        }
    }

    fun onLanguageClick(language: LanguageModel) {
        viewModelScope.launch {
            dataStorePrefs.setLanguageHandShown(true)

            val updatedList = _uiState.value.languages.map {
                it.copy(isSelected = it.languageCode == language.languageCode)
            }

            tempSelectedLanguage = language.copy(isSelected = true)

            _uiState.value = _uiState.value.copy(
                languages = updatedList,
                selectedLanguage = tempSelectedLanguage,
                isSaveEnabled = true,
                showHandAnimation = false
            )
        }
    }

    fun saveLanguage(onSaved: () -> Unit) {
        val selected = tempSelectedLanguage ?: return

        viewModelScope.launch {
            dataStorePrefs.setSelectedLanguageCode(selected.languageCode)
            dataStorePrefs.setSelectedLanguageData(Gson().toJson(selected))
            dataStorePrefs.setLanguageSelected(true)
            dataStorePrefs.setLanguageHandShown(true)

            onSaved()
        }
    }
}
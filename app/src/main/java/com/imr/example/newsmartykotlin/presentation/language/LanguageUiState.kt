package com.imr.example.newsmartykotlin.presentation.language

import com.imr.example.newsmartykotlin.domain.model.LanguageModel

data class LanguageUiState(
    val languages: List<LanguageModel> = emptyList(),
    val selectedLanguage: LanguageModel? = null,
    val isSaveEnabled: Boolean = false,
    val showHandAnimation: Boolean = false
)
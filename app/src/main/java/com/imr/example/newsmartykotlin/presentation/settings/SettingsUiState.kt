package com.imr.example.newsmartykotlin.presentation.settings

data class SettingsUiState(
    val selectedLanguageName: String = "English",
    val isPremium: Boolean = false
)

package com.imr.example.newsmartykotlin.presentation.saved

data class SavedUiState(
    val imagePath: String = "",
    val isForPassport: Boolean = false,
    val isForBgRemover: Boolean = false,
    val isForSuitChanger: Boolean = false,
    val countryId: String = "",
    val documentType: String = ""
)
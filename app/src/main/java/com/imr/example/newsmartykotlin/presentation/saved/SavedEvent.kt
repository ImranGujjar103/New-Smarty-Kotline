package com.imr.example.newsmartykotlin.presentation.saved

sealed class SavedEvent {
    data object NavigateTryMore : SavedEvent()
    data class NavigatePassportTryMore(
        val countryId: String,
        val documentType: String
    ) : SavedEvent()
    data class ShareImage(val imagePath: String) : SavedEvent()
}
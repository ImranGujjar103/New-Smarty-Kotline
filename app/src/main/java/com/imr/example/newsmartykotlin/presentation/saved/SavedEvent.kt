package com.imr.example.newsmartykotlin.presentation.saved

sealed class SavedEvent {
    data object NavigateTryMore : SavedEvent()
    data object NavigateBgRemoverTryMore : SavedEvent()
    data object NavigateSuitTryMore : SavedEvent()
    data class NavigatePassportTryMore(
        val countryId: String,
        val documentType: String
    ) : SavedEvent()
    data class ShareImage(
        val imagePath: String,
        val packageName: String? = null,
        val platformName: String? = null
    ) : SavedEvent()
}
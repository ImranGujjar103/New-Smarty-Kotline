package com.imr.example.newsmartykotlin.presentation.gallery

sealed interface GalleryEvent {

    data object NavigateBack : GalleryEvent

    data class NavigateToCropFace(
        val suitUrl: String,
        val imageUri: String
    ) : GalleryEvent

    data class NavigateToBgRemoverCrop(
        val imageUri: String
    ) : GalleryEvent

    data class NavigateToPassportCrop(
        val imageUri: String,
        val countryId: String,
        val documentType: String
    ) : GalleryEvent
}
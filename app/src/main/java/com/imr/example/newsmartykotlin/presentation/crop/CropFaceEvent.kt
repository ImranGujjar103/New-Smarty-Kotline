package com.imr.example.newsmartykotlin.presentation.crop

sealed interface CropFaceEvent {
    data object NavigateBack : CropFaceEvent

    data class NavigateToSuitBgRemove(
        val suitId: String,
        val croppedImageUri: String
    ) : CropFaceEvent

    data class NavigateToBgRemoverRemove(
        val croppedImageUri: String
    ) : CropFaceEvent
}
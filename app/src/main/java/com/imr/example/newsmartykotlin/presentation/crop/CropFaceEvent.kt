package com.imr.example.newsmartykotlin.presentation.crop

sealed interface CropFaceEvent {
    data object NavigateBack : CropFaceEvent

    data class NavigateNext(
        val suitId: String,
        val croppedImageUri: String
    ) : CropFaceEvent
}
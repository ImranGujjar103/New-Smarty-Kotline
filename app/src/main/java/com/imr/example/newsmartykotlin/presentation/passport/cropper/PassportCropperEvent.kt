package com.imr.example.newsmartykotlin.presentation.passport.cropper

sealed interface PassportCropperEvent {

    data object NavigateBack : PassportCropperEvent

    data class NavigateToBackgroundRemove(
        val croppedImageUri: String,
        val countryId: String,
        val documentType: String
    ) : PassportCropperEvent
}
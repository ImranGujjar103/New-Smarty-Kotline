package com.imr.example.newsmartykotlin.presentation.passport.result

sealed interface PassportResultEvent {

    data object NavigateBack : PassportResultEvent

    data object TryAgain : PassportResultEvent

    data class NavigateToBackground(
        val imageUri: String
    ) : PassportResultEvent

    data class SaveImage(
        val imageUri: String
    ) : PassportResultEvent
}
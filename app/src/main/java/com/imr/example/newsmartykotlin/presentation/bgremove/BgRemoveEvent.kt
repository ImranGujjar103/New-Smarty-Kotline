package com.imr.example.newsmartykotlin.presentation.bgremove

sealed interface BgRemoveEvent {
    data class NavigateToSuitEditor(
        val suitId: String,
        val removedBgImageUri: String
    ) : BgRemoveEvent

    data class NavigateToBgRemoverEditor(
        val removedBgImageUri: String
    ) : BgRemoveEvent
}
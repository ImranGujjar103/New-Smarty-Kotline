package com.imr.example.newsmartykotlin.presentation.bgremove

sealed interface BgRemoveEvent {

    data class NavigateNext(
        val suitId: String,
        val removedBgImageUri: String
    ) : BgRemoveEvent
}
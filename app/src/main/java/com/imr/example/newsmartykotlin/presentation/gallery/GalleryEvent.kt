package com.imr.example.newsmartykotlin.presentation.gallery

sealed interface GalleryEvent {
    data object NavigateBack : GalleryEvent
}
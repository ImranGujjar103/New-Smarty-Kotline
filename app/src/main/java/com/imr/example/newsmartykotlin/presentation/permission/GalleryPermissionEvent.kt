package com.imr.example.newsmartykotlin.presentation.permission

sealed interface GalleryPermissionEvent {
    data object RequestPermission : GalleryPermissionEvent
    data object OpenSettings : GalleryPermissionEvent
    data object NavigateGallery : GalleryPermissionEvent
}
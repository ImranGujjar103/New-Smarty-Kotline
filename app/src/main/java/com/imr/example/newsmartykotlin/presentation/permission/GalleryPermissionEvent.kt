package com.imr.example.newsmartykotlin.presentation.permission

sealed interface GalleryPermissionEvent {

    data object RequestPermission : GalleryPermissionEvent

    data object OpenSettings : GalleryPermissionEvent

    data object NavigateGallery : GalleryPermissionEvent

    data object NavigateBgRemoverGallery : GalleryPermissionEvent

    data object NavigateBackgroundGallery : GalleryPermissionEvent

    data class NavigatePassportGallery(
        val countryId: String,
        val documentType: String
    ) : GalleryPermissionEvent
}
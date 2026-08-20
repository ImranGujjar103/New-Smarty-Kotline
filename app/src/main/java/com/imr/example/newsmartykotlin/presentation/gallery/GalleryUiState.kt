package com.imr.example.newsmartykotlin.presentation.gallery

import com.imr.example.newsmartykotlin.domain.model.GalleryImage

data class GalleryUiState(
    val isLoading: Boolean = false,
    val images: List<GalleryImage> = emptyList(),
    val selectedFolderName: String = "My Photos",
    val errorMessage: String? = null,
    val isForBgRemover: Boolean = false,
    val isLimitedAccess: Boolean = false
) {
    val folders: List<String>
        get() = listOf("My Photos") + images.map { it.folderName }.distinct()

    val filteredImages: List<GalleryImage>
        get() = if (selectedFolderName == "My Photos") {
            images
        } else {
            images.filter { it.folderName == selectedFolderName }
        }
}
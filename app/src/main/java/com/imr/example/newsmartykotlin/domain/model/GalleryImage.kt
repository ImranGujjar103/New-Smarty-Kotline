package com.imr.example.newsmartykotlin.domain.model

data class GalleryImage(
    val id: Long,
    val uri: String,
    val fileName: String,
    val dateAddedMillis: Long,
    val folderId: String,
    val folderName: String
)
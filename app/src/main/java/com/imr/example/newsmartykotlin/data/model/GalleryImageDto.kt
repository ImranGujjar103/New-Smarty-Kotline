package com.imr.example.newsmartykotlin.data.model

data class GalleryImageDto(
    val id: Long,
    val uri: String,
    val fileName: String,
    val dateAddedMillis: Long,
    val folderId: String,
    val folderName: String
)
package com.imr.example.newsmartykotlin.data.local

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.imr.example.newsmartykotlin.data.model.GalleryImageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryLocalDataSource(
    private val context: Context
) {

    suspend fun getGalleryImages(): List<GalleryImageDto> {
        return withContext(Dispatchers.IO) {
            val images = mutableListOf<GalleryImageDto>()

            val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)

                    images.add(
                        GalleryImageDto(
                            id = id,
                            uri = ContentUris.withAppendedId(collection, id).toString(),
                            fileName = cursor.getString(nameColumn) ?: "Unknown",
                            dateAddedMillis = cursor.getLong(dateColumn) * 1000L,
                            folderId = cursor.getString(bucketIdColumn) ?: "unknown",
                            folderName = cursor.getString(bucketNameColumn) ?: "My Photos"
                        )
                    )
                }
            }

            images
        }
    }
}
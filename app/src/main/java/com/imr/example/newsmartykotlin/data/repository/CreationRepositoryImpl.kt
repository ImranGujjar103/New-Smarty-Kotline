package com.imr.example.newsmartykotlin.data.repository

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.Creation
import com.imr.example.newsmartykotlin.domain.repository.CreationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreationRepositoryImpl(
    private val context: Context
) : CreationRepository {

    override suspend fun getCreations(): List<Creation> = withContext(Dispatchers.IO) {
        val creations = mutableListOf<Creation>()
        val appFolder = context.getString(R.string.app_name)

        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = mutableListOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Images.Media.RELATIVE_PATH)
            } else {
                add(MediaStore.Images.Media.DATA)
            }
        }.toTypedArray()

        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        } else {
            "${MediaStore.Images.Media.DATA} LIKE ?"
        }

        val selectionArgs = arrayOf("%$appFolder%")

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(nameColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                creations.add(
                    Creation(
                        id = id,
                        uri = contentUri.toString(),
                        displayName = displayName
                    )
                )
            }
        }
        creations
    }

    override suspend fun deleteCreation(creation: Creation): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = android.net.Uri.parse(creation.uri)
            context.contentResolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

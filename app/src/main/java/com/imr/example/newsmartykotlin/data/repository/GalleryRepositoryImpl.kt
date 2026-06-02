package com.imr.example.newsmartykotlin.data.repository

import com.imr.example.newsmartykotlin.data.local.GalleryLocalDataSource
import com.imr.example.newsmartykotlin.data.local.PermissionDataStore
import com.imr.example.newsmartykotlin.domain.model.GalleryImage
import com.imr.example.newsmartykotlin.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow

class GalleryRepositoryImpl(
    private val galleryLocalDataSource: GalleryLocalDataSource,
    private val permissionDataStore: PermissionDataStore
) : GalleryRepository {

    override suspend fun getGalleryImages(): List<GalleryImage> {
        return galleryLocalDataSource.getGalleryImages().map { dto ->
            GalleryImage(
                id = dto.id,
                uri = dto.uri,
                fileName = dto.fileName,
                dateAddedMillis = dto.dateAddedMillis,
                folderId = dto.folderId,
                folderName = dto.folderName
            )
        }
    }

    override fun getPermissionDenyCount(): Flow<Int> {
        return permissionDataStore.getDenyCount()
    }

    override suspend fun incrementPermissionDenyCount() {
        permissionDataStore.incrementDenyCount()
    }

    override suspend fun resetPermissionDenyCount() {
        permissionDataStore.resetDenyCount()
    }
}
package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.domain.model.GalleryImage
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {

    suspend fun getGalleryImages(): List<GalleryImage>

    fun getPermissionDenyCount(): Flow<Int>

    suspend fun incrementPermissionDenyCount()

    suspend fun resetPermissionDenyCount()
}
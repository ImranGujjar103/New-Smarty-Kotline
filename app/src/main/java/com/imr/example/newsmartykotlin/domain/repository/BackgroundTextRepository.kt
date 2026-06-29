package com.imr.example.newsmartykotlin.domain.repository

import android.graphics.Bitmap

interface BackgroundTextRepository {
    suspend fun saveBitmapToCache(bitmap: Bitmap): String
    suspend fun saveBitmapToGallery(bitmap: Bitmap): String
}
package com.imr.example.newsmartykotlin.domain.repository

interface BgRemoveRepository {

    suspend fun removeBackground(
        imageUri: String
    ): String
}
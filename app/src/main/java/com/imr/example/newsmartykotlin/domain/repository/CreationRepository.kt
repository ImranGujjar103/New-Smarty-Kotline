package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.domain.model.Creation

interface CreationRepository {
    suspend fun getCreations(): List<Creation>
    suspend fun deleteCreation(creation: Creation): Boolean
}

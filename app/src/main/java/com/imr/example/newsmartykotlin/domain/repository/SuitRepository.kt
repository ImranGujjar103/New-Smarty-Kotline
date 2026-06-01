package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.domain.model.SuitCategory

interface SuitRepository {
    suspend fun getSuitCategories(): List<SuitCategory>
}
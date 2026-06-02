package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.domain.model.SuitItem

interface PhotoEditorRepository {
    suspend fun getSuitById(suitId: String): SuitItem?
}
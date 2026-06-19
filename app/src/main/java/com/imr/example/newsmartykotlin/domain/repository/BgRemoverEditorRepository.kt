package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.presentation.bgremovereditor.BgEditorBackground

interface BgRemoverEditorRepository {
    suspend fun exportImage(
        removedImageUri: String,
        background: BgEditorBackground,
        flipX: Float
    ): String
}
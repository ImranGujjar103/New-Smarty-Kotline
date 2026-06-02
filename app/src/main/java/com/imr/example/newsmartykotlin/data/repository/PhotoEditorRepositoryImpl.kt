package com.imr.example.newsmartykotlin.data.repository

import com.imr.example.newsmartykotlin.domain.model.SuitItem
import com.imr.example.newsmartykotlin.domain.repository.PhotoEditorRepository

class PhotoEditorRepositoryImpl : PhotoEditorRepository {

    override suspend fun getSuitById(suitId: String): SuitItem? {
        val suits = listOf(
            SuitItem(
                id = "Jackets_02",
                name = "Black Suit",
                imageUrl = "https://your-domain.com/suits/jackets_02.png"
            )
        )

        return suits.find { it.id == suitId }
    }
}
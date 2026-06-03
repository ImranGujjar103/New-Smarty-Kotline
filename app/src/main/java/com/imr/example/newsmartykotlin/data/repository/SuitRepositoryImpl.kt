package com.imr.example.newsmartykotlin.data.repository

import android.content.Context
import com.imr.example.newsmartykotlin.domain.model.SuitCategory
import com.imr.example.newsmartykotlin.domain.model.SuitItem
import com.imr.example.newsmartykotlin.domain.repository.SuitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileNotFoundException

class SuitRepositoryImpl(
    private val context: Context
) : SuitRepository {

    override suspend fun getSuitCategories(): List<SuitCategory> {
        return withContext(Dispatchers.IO) {
            parseOutfitsTsv()
        }
    }

    private fun parseOutfitsTsv(): List<SuitCategory> {
        try {
            val lines = context.assets
                .open("sheets/suits.tsv")
                .bufferedReader()
                .readLines()
                .filter { it.isNotBlank() }

            if (lines.size < 3) return emptyList()

            val categoryLine = lines[0].split("\t")
            val dataLines = lines.drop(2)

            val categories = mutableListOf<SuitCategory>()

            var columnIndex = 1

            while (columnIndex < categoryLine.size) {
                val rawCategory = categoryLine.getOrNull(columnIndex).orEmpty()

                if (rawCategory.isNotBlank()) {
                    val title = rawCategory
                        .substringAfter("(")
                        .substringBefore(")")
                        .ifBlank { rawCategory }

                    val items = dataLines.mapNotNull { line ->
                        val columns = line.split("\t")

                        val name = columns.getOrNull(columnIndex).orEmpty()
                        val link = columns.getOrNull(columnIndex + 1).orEmpty()

                        if (name.isNotBlank() && link.isNotBlank()) {
                            SuitItem(
                                id = name,
                                name = name,
                                suitUrl = link
                            )
                        } else {
                            null
                        }
                    }

                    categories.add(
                        SuitCategory(
                            id = title.lowercase().replace(" ", "_"),
                            title = title,
                            items = items
                        )
                    )
                }

                columnIndex += 2
            }

            return categories

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return emptyList()
        }
    }
}
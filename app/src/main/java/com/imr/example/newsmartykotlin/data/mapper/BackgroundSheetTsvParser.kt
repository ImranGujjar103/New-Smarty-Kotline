package com.imr.example.newsmartykotlin.data.mapper

import com.imr.example.newsmartykotlin.domain.model.BackgroundData
import com.imr.example.newsmartykotlin.domain.model.BackgroundSection
import java.util.Collections.emptyList


class BackgroundSheetTsvParser {

    fun parse(tsv: String): List<BackgroundSection> {
        val rows = tsv
            .lines()
            .filter { it.isNotBlank() }
            .map { it.split("\t") }

        if (rows.size < 3) return emptyList()

        val categoryRow = rows[0]
        val dataRows = rows.drop(2)

        val result = mutableListOf<BackgroundSection>()

        var columnIndex = 1

        while (columnIndex < categoryRow.size) {
            val rawCategoryName = categoryRow.getOrNull(columnIndex).orEmpty()
            val categoryName = extractCategoryName(rawCategoryName)

            if (categoryName.isNotBlank()) {
                val items = dataRows.mapNotNull { row ->
                    val name = row.getOrNull(columnIndex).orEmpty().trim()
                    val link = row.getOrNull(columnIndex + 1).orEmpty().trim()

                    if (name.isNotBlank() && isValidUrl(link)) {
                        BackgroundData(
                            name = name,
                            imageUrl = link
                        )
                    } else {
                        null
                    }
                }

                if (items.isNotEmpty()) {
                    result.add(
                        BackgroundSection(
                            categoryName = categoryName,
                            backgrounds = items
                        )
                    )
                }
            }

            columnIndex += 2
        }

        return result
    }

    private fun isValidUrl(value: String): Boolean {
        return value.startsWith("http://") || value.startsWith("https://")
    }

    private fun extractCategoryName(value: String): String {
        val start = value.indexOf("(")
        val end = value.indexOf(")")

        return if (start != -1 && end != -1 && end > start) {
            value.substring(start + 1, end).trim()
        } else {
            value
                .replace("Catagory", "", ignoreCase = true)
                .replace("Category", "", ignoreCase = true)
                .replace(Regex("\\d+"), "")
                .trim()
        }
    }
}
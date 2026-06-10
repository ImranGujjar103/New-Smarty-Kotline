package com.imr.example.newsmartykotlin.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetSheetReader(
    private val context: Context
) {
    suspend fun readTsv(filePath: String): String = withContext(Dispatchers.IO) {
        context.assets.open(filePath).bufferedReader().use { it.readText() }
    }
}
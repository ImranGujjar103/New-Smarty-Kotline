package com.imr.example.newsmartykotlin.data.remote

import com.imr.example.newsmartykotlin.data.local.AssetSheetReader


class BackgroundRemoteDataSource(
    private val assetSheetReader: AssetSheetReader
) {
    suspend fun fetchBackgroundSheet(): String {
        return assetSheetReader.readTsv("sheets/backgrounds.tsv")
    }
}
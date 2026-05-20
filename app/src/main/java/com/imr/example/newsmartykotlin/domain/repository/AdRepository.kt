package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.data.model.AppConfig
import kotlinx.coroutines.flow.StateFlow

interface AdRepository {
    val appConfig: StateFlow<AppConfig>
    var adSettingsSynced: Boolean
    suspend fun fetchRemoteConfig(): Result<AppConfig>
}
package com.imr.example.newsmartykotlin.domain.repository


import com.imr.example.newsmartykotlin.core.common.Resource
import com.imr.example.newsmartykotlin.domain.model.BackgroundSection
import kotlinx.coroutines.flow.Flow

interface BackgroundRepository {
    fun getBackgrounds(): Flow<Resource<List<BackgroundSection>>>
}
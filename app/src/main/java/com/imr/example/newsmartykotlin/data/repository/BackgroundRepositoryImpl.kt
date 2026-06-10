package com.imr.example.newsmartykotlin.data.repository

import com.imr.example.newsmartykotlin.core.common.Resource
import com.imr.example.newsmartykotlin.data.mapper.BackgroundSheetTsvParser
import com.imr.example.newsmartykotlin.data.remote.BackgroundRemoteDataSource
import com.imr.example.newsmartykotlin.domain.model.BackgroundSection
import com.imr.example.newsmartykotlin.domain.repository.BackgroundRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BackgroundRepositoryImpl(
    private val remoteDataSource: BackgroundRemoteDataSource,
    private val parser: BackgroundSheetTsvParser
) : BackgroundRepository {

    override fun getBackgrounds(): Flow<Resource<List<BackgroundSection>>> = flow {
       emit(Resource.Loading)

        try {
            val tsv = remoteDataSource.fetchBackgroundSheet()
            val backgrounds = parser.parse(tsv)
            emit(Resource.Success(backgrounds))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load backgrounds"))
        }
    }
}
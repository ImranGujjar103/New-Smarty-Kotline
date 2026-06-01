package com.imr.example.newsmartykotlin.domain.repository

import com.imr.example.newsmartykotlin.domain.model.HomeFeature

interface HomeRepository {
    fun getHomeFeatures(): List<HomeFeature>
}
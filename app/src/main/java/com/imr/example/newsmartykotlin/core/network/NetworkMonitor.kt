package com.imr.example.newsmartykotlin.core.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isConnected: Flow<Boolean>
}
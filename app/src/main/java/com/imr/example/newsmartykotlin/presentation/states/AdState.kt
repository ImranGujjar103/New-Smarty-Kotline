package com.imr.example.newsmartykotlin.presentation.states

sealed interface AdState {
    object Idle : AdState
    object FetchingConfig : AdState
    object Loading : AdState
    object Ready : AdState
    data class Failed(val error: String) : AdState
    object Showing : AdState
    object BannerLoaded : AdState
}


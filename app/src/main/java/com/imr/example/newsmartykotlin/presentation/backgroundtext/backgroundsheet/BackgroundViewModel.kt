package com.imr.example.newsmartykotlin.presentation.backgroundtext.backgroundsheet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.imr.example.newsmartykotlin.core.common.Resource
import com.imr.example.newsmartykotlin.core.network.NetworkMonitor
import com.imr.example.newsmartykotlin.domain.model.BackgroundSection
import com.imr.example.newsmartykotlin.domain.usecase.backgroundtext.GetBackgroundsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch




class BackgroundViewModel(
    private val getBackgroundsUseCase: GetBackgroundsUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackgroundUiState())
    val uiState: StateFlow<BackgroundUiState> = _uiState

    private var loadJob: Job? = null

    companion object {
        private const val TAG = "BackgroundVM"
    }

    init {
        observeInternet()
        loadBackgrounds()
    }

    // 🔥 Observe internet changes
    private fun observeInternet() {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { isConnected ->
                Log.d(TAG, "Internet connected: $isConnected")

                if (isConnected && shouldFetchAgain()) {
                    Log.d(TAG, "Internet restored → retrying background fetch")
                    loadBackgrounds()
                }
            }
        }
    }

    // 🔥 Prevent unnecessary re-fetch
    private fun shouldFetchAgain(): Boolean {
        val state = _uiState.value

        return !state.isLoading &&
                state.categories.isEmpty()
    }

    fun loadBackgrounds() {
        if (loadJob?.isActive == true) {
            Log.d(TAG, "Fetch already running, skipping...")
            return
        }

        loadJob = viewModelScope.launch {

            getBackgroundsUseCase().collect { result ->

                when (result) {

                    Resource.Loading -> {
                        Log.d(TAG, "Loading backgrounds...")

                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is Resource.Success -> {
                        Log.d(TAG, "Backgrounds loaded: ${result.data.size} categories")

                        val originalCategories = result.data

                        val allBackgrounds = originalCategories
                            .flatMap { it.backgrounds }
                            .distinctBy { it.imageUrl }

                        val allCategory = BackgroundSection(
                            categoryName = "All",
                            backgrounds = allBackgrounds
                        )

                        val finalCategories = listOf(allCategory) + originalCategories

                        _uiState.value = BackgroundUiState(
                            isLoading = false,
                            categories = finalCategories,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "Failed: ${result.message}")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
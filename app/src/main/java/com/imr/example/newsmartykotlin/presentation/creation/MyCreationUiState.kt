package com.imr.example.newsmartykotlin.presentation.creation

import com.imr.example.newsmartykotlin.domain.model.Creation

data class MyCreationUiState(
    val isLoading: Boolean = false,
    val creations: List<Creation> = emptyList(),
    val selectedCreation: Creation? = null,
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false
)

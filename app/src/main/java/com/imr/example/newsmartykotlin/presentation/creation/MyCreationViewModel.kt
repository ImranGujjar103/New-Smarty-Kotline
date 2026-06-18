package com.imr.example.newsmartykotlin.presentation.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.model.Creation
import com.imr.example.newsmartykotlin.domain.usecase.creation.DeleteCreationUseCase
import com.imr.example.newsmartykotlin.domain.usecase.creation.GetCreationsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyCreationViewModel(
    private val getCreationsUseCase: GetCreationsUseCase,
    private val deleteCreationUseCase: DeleteCreationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyCreationUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MyCreationEvent>()
    val event = _event.asSharedFlow()

    init {
        loadCreations()
    }

    fun loadCreations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val creations = getCreationsUseCase()
                _uiState.update { it.copy(isLoading = false, creations = creations) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onCreationClick(creation: Creation) {
        _uiState.update { it.copy(selectedCreation = creation) }
    }

    fun onBackClick() {
        viewModelScope.launch {
            if (_uiState.value.selectedCreation != null) {
                _uiState.update { it.copy(selectedCreation = null) }
            } else {
                _event.emit(MyCreationEvent.NavigateBack)
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onConfirmDelete() {
        val creation = _uiState.value.selectedCreation ?: return
        viewModelScope.launch {
            val success = deleteCreationUseCase(creation)
            if (success) {
                _uiState.update { it.copy(showDeleteDialog = false, selectedCreation = null) }
                loadCreations()
            } else {
                _uiState.update { it.copy(showDeleteDialog = false, errorMessage = "Failed to delete creation") }
            }
        }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onShareClick() {
        _uiState.value.selectedCreation?.let { creation ->
            viewModelScope.launch {
                _event.emit(MyCreationEvent.ShareCreation(creation))
            }
        }
    }
}

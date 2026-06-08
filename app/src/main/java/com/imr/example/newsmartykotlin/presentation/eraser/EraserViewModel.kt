package com.imr.example.newsmartykotlin.presentation.eraser

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.model.EraseStroke
import com.imr.example.newsmartykotlin.domain.usecase.eraser.CreateEraserPreviewUseCase
import com.imr.example.newsmartykotlin.domain.usecase.eraser.SaveErasedImageUseCase
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class EraserViewModel(
    savedStateHandle: SavedStateHandle,
    private val createEraserPreviewUseCase: CreateEraserPreviewUseCase,
    private val saveErasedImageUseCase: SaveErasedImageUseCase
) : ViewModel()
{

    private val faceImageUri: String =
        savedStateHandle[AppRoutes.Eraser.ARG_FACE_IMAGE_URI] ?: ""


    private val _uiState = MutableStateFlow(
        EraserUiState(
            faceImageUri = faceImageUri,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<EraserEvent>()
    val event = _event.asSharedFlow()

    private val strokes = mutableListOf<EraseStroke>()
    private val redoStrokes = mutableListOf<EraseStroke>()

    init {
        loadPreview()
    }

    private fun loadPreview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                createEraserPreviewUseCase(
                    faceImageUri = faceImageUri
                )
            }.onSuccess { bitmap ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        previewBitmap = bitmap
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }
    private fun updateUndoRedoState() {
        _uiState.update {
            it.copy(
                canUndo = strokes.isNotEmpty(),
                canRedo = redoStrokes.isNotEmpty()
            )
        }
    }

    fun addStroke(stroke: EraseStroke) {
        strokes.add(stroke)
        redoStrokes.clear()
        updateUndoRedoState()
    }
    fun resetAll() {
        strokes.clear()
        redoStrokes.clear()

        _uiState.update {
            it.copy(
                canUndo = false,
                canRedo = false
            )
        }
    }

    fun undo() {
        if (strokes.isNotEmpty()) {
            redoStrokes.add(strokes.removeAt(strokes.lastIndex))
            updateUndoRedoState()
        }
    }

    fun redo() {
        if (redoStrokes.isNotEmpty()) {
            strokes.add(redoStrokes.removeAt(redoStrokes.lastIndex))
            updateUndoRedoState()
        }
    }

    fun onBrushSizeChange(value: Float) {
        _uiState.update { it.copy(brushSize = value) }
    }

    fun onBrushOffsetChange(value: Float) {
        _uiState.update { it.copy(brushOffset = value) }
    }

    fun currentStrokes(): List<EraseStroke> {
        return strokes.toList()
    }

    fun onBackClick() {
        viewModelScope.launch {
            _event.emit(EraserEvent.NavigateBack)
        }
    }

    fun onDoneClick(finalBitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            runCatching {
                saveErasedImageUseCase.saveBitmap(finalBitmap)
            }.onSuccess { uri ->
                _uiState.update { it.copy(isSaving = false) }
                _event.emit(EraserEvent.Done(uri))
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }
}
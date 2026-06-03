package com.imr.example.newsmartykotlin.presentation.eraser

sealed class EraserEvent {
    data object NavigateBack : EraserEvent()
    data class Done(val imageUri: String) : EraserEvent()
}
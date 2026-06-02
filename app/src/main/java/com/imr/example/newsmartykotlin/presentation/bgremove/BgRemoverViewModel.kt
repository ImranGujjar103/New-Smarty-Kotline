package com.imr.example.newsmartykotlin.presentation.bgremove

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes

class BgRemoverViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val suitId: String =
        savedStateHandle[AppRoutes.PhotoEditor.ARG_SUIT_ID] ?: ""

    val croppedImageUri: String =
        savedStateHandle[AppRoutes.PhotoEditor.ARG_CROPPED_IMAGE_URI] ?: ""
}
package com.imr.example.newsmartykotlin.presentation.creation

import com.imr.example.newsmartykotlin.domain.model.Creation

sealed class MyCreationEvent {
    data object NavigateBack : MyCreationEvent()
    data class ShareCreation(val creation: Creation) : MyCreationEvent()
}

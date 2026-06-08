package com.imr.example.newsmartykotlin.presentation.backgroundtext

sealed class BackgroundTextEvent {
    data class Done(val imagePath: String) : BackgroundTextEvent()
}
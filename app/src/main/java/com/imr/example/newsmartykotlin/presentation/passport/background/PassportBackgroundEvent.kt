package com.imr.example.newsmartykotlin.presentation.passport.background

sealed interface PassportBackgroundEvent {
    data object NavigateBack : PassportBackgroundEvent
    data object CaptureAndDone  : PassportBackgroundEvent
}
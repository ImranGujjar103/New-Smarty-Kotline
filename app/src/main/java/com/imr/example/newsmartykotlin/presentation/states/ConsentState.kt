package com.imr.example.newsmartykotlin.presentation.states

sealed interface ConsentState {
    object Idle : ConsentState
    object RequiresConsent : ConsentState
    object Completed : ConsentState
}


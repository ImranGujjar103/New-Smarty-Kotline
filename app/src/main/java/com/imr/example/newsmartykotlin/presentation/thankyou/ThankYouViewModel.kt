package com.imr.example.newsmartykotlin.presentation.thankyou

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ThankYouViewModel : ViewModel() {

    private val _exitApp = MutableSharedFlow<Unit>()
    val exitApp = _exitApp.asSharedFlow()

    init {
        startExitTimer()
    }

    private fun startExitTimer() {
        viewModelScope.launch {
            delay(2000.milliseconds)
            _exitApp.emit(Unit)
        }
    }
}

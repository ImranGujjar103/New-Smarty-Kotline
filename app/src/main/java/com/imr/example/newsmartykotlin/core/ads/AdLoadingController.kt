package com.imr.example.newsmartykotlin.core.ads

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AdLoadingController {

    fun show(
        scope: CoroutineScope,
        delayMillis: Long = 800L,
        callback: () -> Unit
    ) {
        AdLoadingState.show()

        scope.launch {
            delay(delayMillis.milliseconds)
            callback()
            // We don't hide here anymore, AdManager will hide it on ad dismiss or fail
        }
    }
}

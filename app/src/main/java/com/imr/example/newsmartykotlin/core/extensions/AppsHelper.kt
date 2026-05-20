package com.imr.example.newsmartykotlin.core.extensions

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return dateFormat.format(Date())
}
// Add to your Extension.kt or BaseFragment
inline fun Fragment.safeLaunch(crossinline block: suspend CoroutineScope.() -> Unit) {
    if (isAdded && view != null) {
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                block()
            }
        } catch (e: IllegalStateException) {
            Log.e(this::class.simpleName, "Cannot launch coroutine: ${e.message}")
        }
    }
}
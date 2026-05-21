package com.imr.example.newsmartykotlin.core.extensions

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import android.os.Build
import android.view.WindowInsets
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.imr.example.newsmartykotlin.R

fun ComponentActivity.setupLightSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.show(WindowInsets.Type.statusBars())
        window.insetsController?.show(WindowInsets.Type.navigationBars())
    } else {
        WindowCompat.getInsetsController(window, window.decorView)
            .show(WindowInsetsCompat.Type.systemBars())
    }

    enableEdgeToEdge(
        SystemBarStyle.light(ContextCompat.getColor(this,R.color.background_color), ContextCompat.getColor(this,R.color.background_color)),
        SystemBarStyle.light(ContextCompat.getColor(this,R.color.background_color), ContextCompat.getColor(this,R.color.background_color))
    )

    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = true
        isAppearanceLightNavigationBars = true
    }
}
package com.imr.example.newsmartykotlin.presentation.navigation

sealed class AppRoutes(val route: String) {

    data object Splash : AppRoutes("splash")
    data object Premium : AppRoutes("premium")
    data object Home : AppRoutes("home")

    data object Language : AppRoutes("language/{fromSplash}") {
        fun createRoute(fromSplash: Boolean) = "language/$fromSplash"
    }

    data object Onboarding : AppRoutes("onboarding")
    data object Settings : AppRoutes("settings")

    data object Pin : AppRoutes("pin/{mode}") {
        fun createRoute(mode: String) = "pin/$mode"

        const val MODE_SET = "set"
        const val MODE_CHANGE = "change"
    }

    data object Feature : AppRoutes("feature/{featureKey}") {
        fun createRoute(featureKey: String) = "feature/$featureKey"
    }
}
package com.imr.example.newsmartykotlin.presentation.navigation

sealed class AppRoutes(val route: String) {

    data object Splash : AppRoutes("splash")
    data object Premium : AppRoutes("premium")
    data object Home : AppRoutes("home")
    data object Onboarding : AppRoutes("onboarding")
    data object Suits : AppRoutes("suits")

    data object Language : AppRoutes("language/{fromSplash}") {
        const val ARG_FROM_SPLASH = "fromSplash"

        fun createRoute(fromSplash: Boolean): String {
            return "language/$fromSplash"
        }
    }
}
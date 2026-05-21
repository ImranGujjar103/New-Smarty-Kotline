package com.imr.example.newsmartykotlin.presentation.navigation

sealed class AppRoutes(val route: String) {

    data object Splash : AppRoutes("splash")
    data object Premium : AppRoutes("premium")
    data object Home : AppRoutes("home")

    data object Language : AppRoutes("language/{fromSplash}") {
        fun createRoute(fromSplash: Boolean) = "language/$fromSplash"
    }

    data object Onboarding : AppRoutes("onboarding")


}
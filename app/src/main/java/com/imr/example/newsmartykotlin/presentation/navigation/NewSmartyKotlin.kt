package com.imr.example.newsmartykotlin.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imr.example.newsmartykotlin.presentation.splash.SplashRoute
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.imr.example.newsmartykotlin.core.ads.AdLoadingState
import com.imr.example.newsmartykotlin.presentation.common.components.AdLoadingOverlay
import com.imr.example.newsmartykotlin.presentation.language.LanguageRoute

/*import com.imr.example.newsmartykotlin.presentation.home.HomeRoute
import com.imr.example.newsmartykotlin.presentation.language.LanguageRoute
import com.imr.example.newsmartykotlin.presentation.onboarding.OnboardingRoute
import com.imr.example.newsmartykotlin.presentation.premium.PremiumRoute
import com.imr.example.newsmartykotlin.presentation.settings.SettingsRoute*/


@Composable
fun NewSmartyKotlin(navController: NavHostController = rememberNavController()) {
   // val navController = rememberNavController()
    val isAdLoading = AdLoadingState.isShowing.collectAsStateWithLifecycle()

    Box {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash.route
        ) {
            composable(AppRoutes.Splash.route) {
                SplashRoute(
                    onNavigateToLanguage = {
/*                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Splash.route) { inclusive = true }
                        }*/
                        navController.navigate(AppRoutes.Language.createRoute(true)) {
                            popUpTo(AppRoutes.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToPremium = {
                        navController.navigate(AppRoutes.Language.createRoute(true)) {
                            popUpTo(AppRoutes.Splash.route) { inclusive = true }
                        }
     /*                   navController.navigate(AppRoutes.Premium.route) {
                            popUpTo(AppRoutes.Splash.route) { inclusive = true }
                        }*/
                    },
                    onNavigateToHome = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.Language.route,
                arguments = listOf(
                    navArgument("fromSplash") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->

                val fromSplash = backStackEntry.arguments?.getBoolean("fromSplash") ?: false

                LanguageRoute(
                    fromSplash = fromSplash,
                    onNavigateToOnboarding = {
                        navController.navigate(AppRoutes.Onboarding.route) {
                            popUpTo(AppRoutes.Language.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Language.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
/*            composable(AppRoutes.Onboarding.route) {
                OnboardingRoute(
                    onNavigateToPremium = {
                        navController.navigate(AppRoutes.Premium.route) {
                            popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }*/

      /*      composable(AppRoutes.Language.route) {
                Log.d("RoutTesting", "newsmartykotlineApp: Language Route")
                // TODO
            }*/
/*
            composable(AppRoutes.Premium.route) {
                PremiumRoute(
                    onCloseClick = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Premium.route) { inclusive = true }
                        }
                    },
                    onTermsClick = {},
                    onPrivacyClick = {},
                    onSubscriptionDetailsClick = {}
                )
            }

            composable(AppRoutes.Home.route) {
                HomeRoute(
                    navController = navController,
                    onProClick = {
                        navController.navigate(AppRoutes.Premium.route)
                    },
                    onSettingsClick = {
                        navController.navigate(AppRoutes.Settings.route)
                    }
                )
            }

            composable(AppRoutes.Settings.route) {
                SettingsRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onGoPremiumClick = {
                        navController.navigate(AppRoutes.Premium.route)
                    },
                    onSetPinClick = {
                        navController.navigate(AppRoutes.Pin.createRoute(AppRoutes.Pin.MODE_SET))
                    },
                    onChangePinClick = {
                        navController.navigate(AppRoutes.Pin.createRoute(AppRoutes.Pin.MODE_CHANGE))
                    },
                    onLanguageClick = {
                        navController.navigate(AppRoutes.Language.createRoute(false))
                    },
                    onPrivacyClick = {},
                    onRateUsClick = {},
                    onShareAppClick = {},
                    onMoreAppClick = {}
                )
            }*/

        }

        if (isAdLoading.value) {
            AdLoadingOverlay()
        }
    }
}
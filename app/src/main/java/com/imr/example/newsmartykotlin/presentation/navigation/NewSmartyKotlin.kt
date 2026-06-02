package com.imr.example.newsmartykotlin.presentation.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.imr.example.newsmartykotlin.core.ads.AdLoadingState
import com.imr.example.newsmartykotlin.presentation.bgremove.BgRemoveScreen
import com.imr.example.newsmartykotlin.presentation.common.components.AdLoadingOverlay
import com.imr.example.newsmartykotlin.presentation.crop.CropFaceScreen
import com.imr.example.newsmartykotlin.presentation.gallery.GalleryScreen
import com.imr.example.newsmartykotlin.presentation.home.HomeRoute
import com.imr.example.newsmartykotlin.presentation.language.LanguageRoute
import com.imr.example.newsmartykotlin.presentation.onboarding.OnboardingRoute
import com.imr.example.newsmartykotlin.presentation.permission.GalleryPermissionScreen
import com.imr.example.newsmartykotlin.presentation.photoeditor.PhotoEditorScreen
import com.imr.example.newsmartykotlin.presentation.premium.PremiumRoute
import com.imr.example.newsmartykotlin.presentation.splash.SplashRoute
import com.imr.example.newsmartykotlin.presentation.suits.SuitRoute


@Composable
fun NewSmartyKotlin(
    navController: NavHostController
) {
    val isAdLoading = AdLoadingState.isShowing.collectAsStateWithLifecycle()

    Box {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash.route
        ) {
            composable(AppRoutes.Splash.route) {
                SplashRoute(
                    onNavigateToLanguage = {
                        navController.navigate(AppRoutes.Language.createRoute(fromSplash = true)) {
                            popUpTo(AppRoutes.Splash.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToPremium = {
                        navController.navigate(AppRoutes.Premium.route) {
                            popUpTo(AppRoutes.Splash.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.Language.route,
                arguments = listOf(
                    navArgument(AppRoutes.Language.ARG_FROM_SPLASH) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->

                val fromSplash = backStackEntry.arguments
                    ?.getBoolean(AppRoutes.Language.ARG_FROM_SPLASH)
                    ?: false

                LanguageRoute(
                    fromSplash = fromSplash,
                    onNavigateToOnboarding = {
                        navController.navigate(AppRoutes.Onboarding.route) {
                            popUpTo(AppRoutes.Language.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Language.route) {
                                inclusive = true
                            }
                        }
                    },
                    onBackClick = {
                        if (fromSplash) {
                            navController.navigate(AppRoutes.Onboarding.route) {
                                popUpTo(AppRoutes.Language.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(AppRoutes.Onboarding.route) {
                OnboardingRoute(
                    onNavigateToPremium = {
                        navController.navigate(AppRoutes.Premium.route) {
                            popUpTo(AppRoutes.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(AppRoutes.Premium.route) {
                PremiumRoute(
                    onCloseClick = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Premium.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(AppRoutes.Home.route) {
                HomeRoute(
                    onNavigateToSuits = {
                        navController.navigate(AppRoutes.Suits.route)
                    }
                )
            }

            composable(AppRoutes.Suits.route) {
                SuitRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToGallery = { suitItem ->
                        navController.navigate(        AppRoutes.GalleryPermission.createRoute(
                            suitId = suitItem.id
                        ))
                    }
                )
            }


            composable(
                route = AppRoutes.GalleryPermission.route,
                arguments = listOf(
                    navArgument(AppRoutes.GalleryPermission.ARG_SUIT_ID) {
                        type = NavType.StringType
                    }
                )
            ) {
                GalleryPermissionScreen(
                    navController = navController
                )
            }

            composable(
                route = AppRoutes.Gallery.route,
                arguments = listOf(
                    navArgument(AppRoutes.Gallery.ARG_SUIT_ID) {
                        type = NavType.StringType
                    }
                )
            ) {
                GalleryScreen(
                    navController = navController
                )
            }

            composable(
                route = AppRoutes.CropFace.route,
                arguments = listOf(
                    navArgument(AppRoutes.CropFace.ARG_SUIT_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.CropFace.ARG_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                CropFaceScreen(
                    navController = navController
                )
            }

            composable(
                route = AppRoutes.BgRemove.route,
                arguments = listOf(
                    navArgument(AppRoutes.BgRemove.ARG_SUIT_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.BgRemove.ARG_CROPPED_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                BgRemoveScreen(
                    navController = navController
                )
            }


            composable(
                route = AppRoutes.PhotoEditor.route,
                arguments = listOf(
                    navArgument(AppRoutes.PhotoEditor.ARG_SUIT_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PhotoEditor.ARG_CROPPED_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                PhotoEditorScreen(
                    navController = navController
                )
            }
        }

        if (isAdLoading.value) {
            AdLoadingOverlay()
        }
    }
}
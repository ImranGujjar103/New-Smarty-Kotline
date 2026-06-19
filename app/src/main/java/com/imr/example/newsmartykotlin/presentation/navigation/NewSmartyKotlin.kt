package com.imr.example.newsmartykotlin.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.imr.example.newsmartykotlin.core.ads.AdLoadingState
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.presentation.backgroundtext.BackgroundTextScreen
import com.imr.example.newsmartykotlin.presentation.bgremove.BgRemoveScreen
import com.imr.example.newsmartykotlin.presentation.bgremovereditor.BgRemoverEditorScreen
import com.imr.example.newsmartykotlin.presentation.common.components.AdLoadingOverlay
import com.imr.example.newsmartykotlin.presentation.creation.MyCreationScreen
import com.imr.example.newsmartykotlin.presentation.crop.CropFaceScreen
import com.imr.example.newsmartykotlin.presentation.eraser.EraserScreen
import com.imr.example.newsmartykotlin.presentation.gallery.GalleryScreen
import com.imr.example.newsmartykotlin.presentation.home.HomeRoute
import com.imr.example.newsmartykotlin.presentation.language.LanguageRoute
import com.imr.example.newsmartykotlin.presentation.onboarding.OnboardingRoute
import com.imr.example.newsmartykotlin.presentation.passport.PassportCountryRoute
import com.imr.example.newsmartykotlin.presentation.passport.PassportDetailRoute
import com.imr.example.newsmartykotlin.presentation.passport.background.PassportBackgroundScreen
import com.imr.example.newsmartykotlin.presentation.passport.cropper.PassportCropperScreen
import com.imr.example.newsmartykotlin.presentation.passport.result.PassportResultScreen
import com.imr.example.newsmartykotlin.presentation.permission.GalleryPermissionHelper
import com.imr.example.newsmartykotlin.presentation.permission.GalleryPermissionScreen
import com.imr.example.newsmartykotlin.presentation.photoeditor.EditorAction
import com.imr.example.newsmartykotlin.presentation.photoeditor.PhotoEditorScreen
import com.imr.example.newsmartykotlin.presentation.premium.PremiumRoute
import com.imr.example.newsmartykotlin.presentation.saved.SavedScreen
import com.imr.example.newsmartykotlin.presentation.settings.SettingsScreen
import com.imr.example.newsmartykotlin.presentation.splash.SplashRoute
import com.imr.example.newsmartykotlin.presentation.suits.SuitRoute

@Composable
fun NewSmartyKotlin(
    navController: NavHostController
) {
    val context = LocalContext.current
    val isAdLoading = AdLoadingState.isShowing.collectAsStateWithLifecycle()

    Box {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash.route
        ) {
            composable(AppRoutes.Splash.route) {
                SplashRoute(
                    onNavigateToLanguage = {
                        navController.navigate(AppRoutes.Language.createRoute(true)) {
                            popUpTo(AppRoutes.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToPremium = {
                        navController.navigate(AppRoutes.Premium.route) {
                            popUpTo(AppRoutes.Splash.route) { inclusive = true }
                        }
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
                            popUpTo(AppRoutes.Language.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Language.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        if (fromSplash) {
                            navController.navigate(AppRoutes.Onboarding.route) {
                                popUpTo(AppRoutes.Language.route) { inclusive = true }
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
                            popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoutes.Premium.route) {
                PremiumRoute(
                    onCloseClick = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Premium.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoutes.Home.route) {
                HomeRoute(
                    onNavigateToSuits = {
                        navController.navigate(AppRoutes.Suits.route)
                    },
                    onNavigateToBgChanger = {
                        navController.navigate(AppRoutes.GalleryForBgRemover.createRoute())
                    },
                    onNavigateToPassportPic = {
                        navController.navigate(AppRoutes.PassportCountry.route)
                    },
                    onNavigateToMyCreation = {
                        navController.navigate(AppRoutes.MyCreation.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(AppRoutes.Settings.route)
                    },
                    onNavigateToPremium = {
                        navController.navigate(AppRoutes.Premium.route)
                    }
                )
            }

            composable(
                route = AppRoutes.Suits.route,
                arguments = listOf(
                    navArgument(AppRoutes.Suits.ARG_FROM_EDITOR) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val fromEditor = backStackEntry.arguments
                    ?.getBoolean(AppRoutes.Suits.ARG_FROM_EDITOR)
                    ?: false

                SuitRoute(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToGallery = { suitItem ->
                        if (fromEditor) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(SELECTED_SUIT_URL_KEY, suitItem.suitUrl)

                            navController.popBackStack()
                        } else {
                            if (GalleryPermissionHelper.hasGalleryPermission(context)) {
                                navController.navigate(
                                    AppRoutes.Gallery.createRoute(suitItem.suitUrl)
                                )
                            } else {
                                navController.navigate(
                                    AppRoutes.GalleryPermission.createRoute(suitItem.suitUrl)
                                )
                            }
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.GalleryPermission.route,
                arguments = listOf(
                    navArgument(AppRoutes.GalleryPermission.ARG_SUIT_URL) {
                        type = NavType.StringType
                    }
                )
            ) {
                GalleryPermissionScreen(navController = navController)
            }

            composable(
                route = AppRoutes.Gallery.route,
                arguments = listOf(
                    navArgument(AppRoutes.Gallery.ARG_SUIT_URL) {
                        type = NavType.StringType
                    }
                )
            ) {
                GalleryScreen(navController = navController)
            }

            composable(
                route = AppRoutes.CropFace.route,
                arguments = listOf(
                    navArgument(AppRoutes.CropFace.ARG_SUIT_URL) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.CropFace.ARG_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                CropFaceScreen(navController = navController)
            }

            composable(
                route = AppRoutes.BgRemove.route,
                arguments = listOf(
                    navArgument(AppRoutes.BgRemove.ARG_SUIT_URL) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.BgRemove.ARG_CROPPED_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                BgRemoveScreen(navController = navController)
            }

            composable(
                route = AppRoutes.PhotoEditor.route,
                arguments = listOf(
                    navArgument(AppRoutes.PhotoEditor.ARG_SUIT_URL) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PhotoEditor.ARG_CROPPED_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                PhotoEditorScreen(
                    navController = navController,
                    onActionClick =  { action ->
                        when (action) {
                            EditorAction.OUTFITS -> {
                                navController.navigate(
                                    AppRoutes.Suits.createRoute(fromEditor = true)
                                )
                            }

                            EditorAction.ERASER -> Unit
                            EditorAction.FACE_FLIP -> Unit
                            EditorAction.SUIT_FLIP -> Unit
                        }
                    }
                )
            }

            composable(
                route = "${AppRoutes.Eraser.route}/{${AppRoutes.Eraser.ARG_FACE_IMAGE_URI}}",
                arguments = listOf(
                    navArgument(AppRoutes.Eraser.ARG_FACE_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                EraserScreen(navController = navController)
            }

            composable(
                route = "${AppRoutes.BackgroundText.route}/{${AppRoutes.BackgroundText.ARG_IMAGE_PATH}}",
                arguments = listOf(
                    navArgument(AppRoutes.BackgroundText.ARG_IMAGE_PATH) {
                        type = NavType.StringType
                    }
                )
            ) {
                BackgroundTextScreen(navController = navController)
            }

            composable(AppRoutes.GalleryForBackground.route) {
                GalleryScreen(
                    navController = navController,
                    isForBackground = true
                )
            }

            composable(AppRoutes.MyCreation.route) {
                MyCreationScreen(navController = navController)
            }

            composable(AppRoutes.Settings.route) {
                SettingsScreen(navController = navController)
            }

            composable(
                route = AppRoutes.GalleryForBgRemover.route,
                arguments = listOf(
                    navArgument(AppRoutes.GalleryForBgRemover.ARG_IS_BG_REMOVER) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                GalleryScreen(navController = navController)
            }

            composable(
                route = AppRoutes.CropForBgRemover.route,
                arguments = listOf(
                    navArgument(AppRoutes.CropForBgRemover.ARG_IMAGE_URI) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.CropForBgRemover.ARG_IS_BG_REMOVER) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                CropFaceScreen(navController = navController)
            }

            composable(
                route = AppRoutes.BgRemoveForBgRemover.route,
                arguments = listOf(
                    navArgument(AppRoutes.BgRemoveForBgRemover.ARG_CROPPED_IMAGE_URI) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.BgRemoveForBgRemover.ARG_IS_BG_REMOVER) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                BgRemoveScreen(navController = navController)
            }

            composable(
                route = AppRoutes.BgRemoverEditor.route,
                arguments = listOf(
                    navArgument(AppRoutes.BgRemoverEditor.ARG_REMOVED_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                BgRemoverEditorScreen(navController = navController)
            }



            composable(AppRoutes.PassportCountry.route) {
                PassportCountryRoute(
                    onBackClick = { navController.popBackStack() },
                    onCountryClick = { country, selectedType ->
                        navController.navigate(
                            AppRoutes.PassportDetail.createRoute(
                                countryId = country.id,
                                documentType = selectedType.name
                            )
                        )
                    }
                )
            }

            composable(
                route = AppRoutes.PassportDetail.route,
                arguments = listOf(
                    navArgument(AppRoutes.PassportDetail.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportDetail.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportDetail.ARG_FINAL_IMAGE_URI) {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    }
                )
            ) { backStackEntry ->

                val countryId = backStackEntry.arguments
                    ?.getString(AppRoutes.PassportDetail.ARG_COUNTRY_ID)
                    .orEmpty()

                val documentType = backStackEntry.arguments
                    ?.getString(AppRoutes.PassportDetail.ARG_DOCUMENT_TYPE)
                    ?.let { DocumentType.valueOf(it) }
                    ?: DocumentType.PASSPORT

                PassportDetailRoute(
                    countryId = countryId,
                    selectedType = documentType,
                    onBackClick = { navController.popBackStack() },
                    onCameraImageCaptured = { imageUri ->
                        navController.navigate(
                            AppRoutes.PassportCropper.createRoute(
                                imageUri = imageUri,
                                countryId = countryId,
                                documentType = documentType.name
                            )
                        )
                    },
                    onGalleryClick = {
                        if (GalleryPermissionHelper.hasGalleryPermission(context)) {
                            navController.navigate(
                                AppRoutes.GalleryForPassport.createRoute(
                                    countryId = countryId,
                                    documentType = documentType.name
                                )
                            )
                        } else {
                            navController.navigate(
                                AppRoutes.GalleryPermissionForPassport.createRoute(
                                    countryId = countryId,
                                    documentType = documentType.name
                                )
                            )
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.GalleryForPassport.route,
                arguments = listOf(
                    navArgument(AppRoutes.GalleryForPassport.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.GalleryForPassport.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.GalleryForPassport.ARG_IS_FOR_PASSPORT) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                GalleryScreen(navController = navController)
            }

            composable(
                route = AppRoutes.PassportCropper.route,
                arguments = listOf(
                    navArgument(AppRoutes.PassportCropper.ARG_IMAGE_URI) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportCropper.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportCropper.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                    }
                )
            ) {
                PassportCropperScreen(navController = navController)
            }

            composable(
                route = AppRoutes.PassportBgRemove.route,
                arguments = listOf(
                    navArgument(AppRoutes.PassportBgRemove.ARG_CROPPED_IMAGE_URI) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportBgRemove.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportBgRemove.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportBgRemove.ARG_IS_FOR_PASSPORT) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                BgRemoveScreen(navController = navController)
            }

            composable(
                route = AppRoutes.GalleryPermissionForPassport.route,
                arguments = listOf(
                    navArgument(AppRoutes.GalleryPermissionForPassport.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.GalleryPermissionForPassport.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.GalleryPermissionForPassport.ARG_IS_FOR_PASSPORT) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                GalleryPermissionScreen(navController = navController)
            }

            composable(
                route = AppRoutes.PassportResult.route,
                arguments = listOf(
                    navArgument(AppRoutes.PassportResult.ARG_IMAGE_URI) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportResult.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportResult.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                    }
                )
            ) {
                PassportResultScreen(navController = navController)
            }

            composable(
                route = AppRoutes.Background.route,
                arguments = listOf(
                    navArgument(AppRoutes.Background.ARG_IMAGE_URI) {
                        type = NavType.StringType
                    }
                )
            ) {
                PassportBackgroundScreen(
                    navController = navController
                )
            }

            composable(
                route = AppRoutes.PassportTryMoreDetail.route,
                arguments = listOf(
                    navArgument(AppRoutes.PassportTryMoreDetail.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                    },
                    navArgument(AppRoutes.PassportTryMoreDetail.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val countryId = backStackEntry.arguments
                    ?.getString(AppRoutes.PassportTryMoreDetail.ARG_COUNTRY_ID)
                    .orEmpty()

                val documentType = DocumentType.PASSPORT

                PassportDetailRoute(
                    countryId = countryId,
                    selectedType = documentType,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCameraImageCaptured = { imageUri ->
                        navController.navigate(
                            AppRoutes.PassportCropper.createRoute(
                                imageUri = imageUri,
                                countryId = countryId,
                                documentType = documentType.name
                            )
                        )
                    },
                    onGalleryClick = {
                        if (GalleryPermissionHelper.hasGalleryPermission(context)) {
                            navController.navigate(
                                AppRoutes.GalleryForPassport.createRoute(
                                    countryId = countryId,
                                    documentType = documentType.name
                                )
                            )
                        } else {
                            navController.navigate(
                                AppRoutes.GalleryPermissionForPassport.createRoute(
                                    countryId = countryId,
                                    documentType = documentType.name
                                )
                            )
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.Saved.route,
                arguments = listOf(
                    navArgument(AppRoutes.Saved.ARG_IMAGE_PATH) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument(AppRoutes.Saved.ARG_IS_FOR_PASSPORT) {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument(AppRoutes.Saved.ARG_COUNTRY_ID) {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument(AppRoutes.Saved.ARG_DOCUMENT_TYPE) {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument(AppRoutes.Saved.ARG_IS_FOR_BG_REMOVER) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                SavedScreen(navController = navController)
            }

        }

        if (isAdLoading.value) {
            AdLoadingOverlay()
        }
    }
}
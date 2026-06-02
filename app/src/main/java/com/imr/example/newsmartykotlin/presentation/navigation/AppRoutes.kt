package com.imr.example.newsmartykotlin.presentation.navigation

import android.net.Uri

sealed class AppRoutes(val route: String) {

    data object Splash : AppRoutes("splash")
    data object Premium : AppRoutes("premium")
    data object Home : AppRoutes("home")
    data object Onboarding : AppRoutes("onboarding")
    data object Suits : AppRoutes("suits")

    //data object Gallery : AppRoutes("gallery")

    data object Language : AppRoutes("language/{fromSplash}") {
        const val ARG_FROM_SPLASH = "fromSplash"

        fun createRoute(fromSplash: Boolean): String {
            return "language/$fromSplash"
        }
    }

    data object GalleryPermission : AppRoutes("gallery_permission/{suitId}") {
        const val ARG_SUIT_ID = "suitId"

        fun createRoute(suitId: String): String {
            return "gallery_permission/$suitId"
        }
    }

    data object Gallery : AppRoutes("gallery/{suitId}") {
        const val ARG_SUIT_ID = "suitId"

        fun createRoute(suitId: String): String {
            return "gallery/$suitId"
        }
    }



    data object CropFace : AppRoutes("crop_face/{suitId}/{imageUri}") {
        const val ARG_SUIT_ID = "suitId"
        const val ARG_IMAGE_URI = "imageUri"

        fun createRoute(
            suitId: String,
            imageUri: String
        ): String {
            return "crop_face/$suitId/${Uri.encode(imageUri)}"
        }
    }

    data object PhotoEditor : AppRoutes("photo_editor/{suitId}/{croppedImageUri}") {
        const val ARG_SUIT_ID = "suitId"
        const val ARG_CROPPED_IMAGE_URI = "croppedImageUri"

        fun createRoute(
            suitId: String,
            croppedImageUri: String
        ): String {
            return "photo_editor/$suitId/${Uri.encode(croppedImageUri)}"
        }
    }

    data object BgRemove : AppRoutes("bg_remove/{suitId}/{croppedImageUri}") {
        const val ARG_SUIT_ID = "suitId"
        const val ARG_CROPPED_IMAGE_URI = "croppedImageUri"

        fun createRoute(
            suitId: String,
            croppedImageUri: String
        ): String {
            return "bg_remove/$suitId/${Uri.encode(croppedImageUri)}"
        }
    }
}
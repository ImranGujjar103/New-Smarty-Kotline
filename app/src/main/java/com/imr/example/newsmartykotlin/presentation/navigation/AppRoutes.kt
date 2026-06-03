package com.imr.example.newsmartykotlin.presentation.navigation

import android.net.Uri

sealed class AppRoutes(val route: String) {

    data object Splash : AppRoutes("splash")
    data object Premium : AppRoutes("premium")
    data object Home : AppRoutes("home")
    data object Onboarding : AppRoutes("onboarding")

    data object Suits : AppRoutes("suits?fromEditor={fromEditor}") {
        const val ARG_FROM_EDITOR = "fromEditor"

        fun createRoute(fromEditor: Boolean = false): String {
            return "suits?fromEditor=$fromEditor"
        }
    }
    data object Language : AppRoutes("language/{fromSplash}") {
        const val ARG_FROM_SPLASH = "fromSplash"

        fun createRoute(fromSplash: Boolean): String {
            return "language/$fromSplash"
        }
    }

    data object GalleryPermission : AppRoutes("gallery_permission/{suitUrl}") {
        const val ARG_SUIT_URL = "suitUrl"

        fun createRoute(suitUrl: String): String {
            return "gallery_permission/${Uri.encode(suitUrl)}"
        }
    }

    data object Gallery : AppRoutes("gallery/{suitUrl}") {
        const val ARG_SUIT_URL = "suitUrl"

        fun createRoute(suitUrl: String): String {
            return "gallery/${Uri.encode(suitUrl)}"
        }
    }

    data object CropFace : AppRoutes("crop_face/{suitUrl}/{imageUri}") {
        const val ARG_SUIT_URL = "suitUrl"
        const val ARG_IMAGE_URI = "imageUri"

        fun createRoute(
            suitUrl: String,
            imageUri: String
        ): String {
            return "crop_face/${Uri.encode(suitUrl)}/${Uri.encode(imageUri)}"
        }
    }

    data object BgRemove : AppRoutes("bg_remove/{suitUrl}/{croppedImageUri}") {
        const val ARG_SUIT_URL = "suitUrl"
        const val ARG_CROPPED_IMAGE_URI = "croppedImageUri"

        fun createRoute(
            suitUrl: String,
            croppedImageUri: String
        ): String {
            return "bg_remove/${Uri.encode(suitUrl)}/${Uri.encode(croppedImageUri)}"
        }
    }

    data object PhotoEditor : AppRoutes("photo_editor/{suitUrl}/{croppedImageUri}") {
        const val ARG_SUIT_URL = "suitUrl"
        const val ARG_CROPPED_IMAGE_URI = "croppedImageUri"

        fun createRoute(
            suitUrl: String,
            croppedImageUri: String
        ): String {
            return "photo_editor/${Uri.encode(suitUrl)}/${Uri.encode(croppedImageUri)}"
        }
    }

    data object Eraser : AppRoutes("eraser/{faceImageUri}/{suitUrl}") {
        const val ARG_FACE_IMAGE_URI = "faceImageUri"
        const val ARG_SUIT_URL = "suitUrl"

        fun createRoute(
            faceImageUri: String,
            suitUrl: String
        ): String {
            return "eraser/${Uri.encode(faceImageUri)}/${Uri.encode(suitUrl)}"
        }
    }
}
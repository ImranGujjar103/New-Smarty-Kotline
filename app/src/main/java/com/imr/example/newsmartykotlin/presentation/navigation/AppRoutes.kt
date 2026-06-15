package com.imr.example.newsmartykotlin.presentation.navigation

import android.net.Uri

sealed class AppRoutes(val route: String) {

    data object Splash : AppRoutes("splash")
    data object Premium : AppRoutes("premium")
    data object Home : AppRoutes("home")
    data object Onboarding : AppRoutes("onboarding")

    data object Suits : AppRoutes("suits?fromEditor={fromEditor}") {
        const val ARG_FROM_EDITOR = "fromEditor"
        fun createRoute(fromEditor: Boolean = false): String =
            "suits?fromEditor=$fromEditor"
    }

    data object Language : AppRoutes("language/{fromSplash}") {
        const val ARG_FROM_SPLASH = "fromSplash"
        fun createRoute(fromSplash: Boolean): String =
            "language/$fromSplash"
    }

    data object GalleryPermission : AppRoutes("gallery_permission/{suitUrl}") {
        const val ARG_SUIT_URL = "suitUrl"
        fun createRoute(suitUrl: String): String =
            "gallery_permission/${Uri.encode(suitUrl)}"
    }

    data object Gallery : AppRoutes("gallery/{suitUrl}") {
        const val ARG_SUIT_URL = "suitUrl"
        fun createRoute(suitUrl: String): String =
            "gallery/${Uri.encode(suitUrl)}"
    }

    data object CropFace : AppRoutes("crop_face/{suitUrl}/{imageUri}") {
        const val ARG_SUIT_URL = "suitUrl"
        const val ARG_IMAGE_URI = "imageUri"

        fun createRoute(
            suitUrl: String,
            imageUri: String
        ): String =
            "crop_face/${Uri.encode(suitUrl)}/${Uri.encode(imageUri)}"
    }

    data object BgRemove : AppRoutes("bg_remove/{suitUrl}/{croppedImageUri}") {
        const val ARG_SUIT_URL = "suitUrl"
        const val ARG_CROPPED_IMAGE_URI = "croppedImageUri"

        fun createRoute(
            suitUrl: String,
            croppedImageUri: String
        ): String =
            "bg_remove/${Uri.encode(suitUrl)}/${Uri.encode(croppedImageUri)}"
    }

    data object PhotoEditor : AppRoutes("photo_editor/{suitUrl}/{croppedImageUri}") {
        const val ARG_SUIT_URL = "suitUrl"
        const val ARG_CROPPED_IMAGE_URI = "croppedImageUri"

        fun createRoute(
            suitUrl: String,
            croppedImageUri: String
        ): String =
            "photo_editor/${Uri.encode(suitUrl)}/${Uri.encode(croppedImageUri)}"
    }

    data object GalleryForBgRemover : AppRoutes(
        "gallery_for_bg_remover?isBgRemover={isBgRemover}"
    ) {
        const val ARG_IS_BG_REMOVER = "isBgRemover"

        fun createRoute(): String =
            "gallery_for_bg_remover?isBgRemover=true"
    }

    data object CropForBgRemover : AppRoutes(
        "crop_for_bg_remover/{imageUri}?isBgRemover={isBgRemover}"
    ) {
        const val ARG_IMAGE_URI = "imageUri"
        const val ARG_IS_BG_REMOVER = "isBgRemover"

        fun createRoute(imageUri: String): String =
            "crop_for_bg_remover/${Uri.encode(imageUri)}?isBgRemover=true"
    }

    data object BgRemoveForBgRemover : AppRoutes(
        "bg_remove_for_bg_remover/{croppedImageUri}?isBgRemover={isBgRemover}"
    ) {
        const val ARG_CROPPED_IMAGE_URI = "croppedImageUri"
        const val ARG_IS_BG_REMOVER = "isBgRemover"

        fun createRoute(croppedImageUri: String): String =
            "bg_remove_for_bg_remover/${Uri.encode(croppedImageUri)}?isBgRemover=true"
    }

    data object BgRemoverEditor : AppRoutes("bg_remover_editor/{removedImageUri}") {
        const val ARG_REMOVED_IMAGE_URI = "removedImageUri"

        fun createRoute(removedImageUri: String): String =
            "bg_remover_editor/${Uri.encode(removedImageUri)}"
    }

    object Eraser {
        const val route = "eraser_screen"
        const val ARG_FACE_IMAGE_URI = "faceImageUri"

        fun createRoute(faceImageUri: String): String =
            "$route/${Uri.encode(faceImageUri)}"
    }

    object BackgroundText {
        const val route = "background_text"
        const val ARG_IMAGE_PATH = "imagePath"

        fun createRoute(imagePath: String): String =
            "$route/${Uri.encode(imagePath)}"
    }

    data object GalleryForBackground : AppRoutes("gallery_for_background")

    data object Saved : AppRoutes("saved/{imagePath}") {
        const val ARG_IMAGE_PATH = "imagePath"

        fun createRoute(imagePath: String): String =
            "saved/${Uri.encode(imagePath)}"
    }

    data object PassportCountry : AppRoutes("passport_country")

    data object PassportDetail : AppRoutes("passport_detail/{countryId}/{documentType}") {
        const val ARG_COUNTRY_ID = "countryId"
        const val ARG_DOCUMENT_TYPE = "documentType"

        fun createRoute(
            countryId: String,
            documentType: String
        ): String {
            return "passport_detail/${Uri.encode(countryId)}/${Uri.encode(documentType)}"
        }
    }

}
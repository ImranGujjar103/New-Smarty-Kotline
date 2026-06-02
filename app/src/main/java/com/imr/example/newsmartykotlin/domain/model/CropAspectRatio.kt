package com.imr.example.newsmartykotlin.domain.model

enum class CropAspectRatio(
    val labelResName: String,
    val ratio: Float?
) {
    FREE(
        labelResName = "Free",
        ratio = null
    ),
    ORIGINAL(
        labelResName = "Original",
        ratio = null
    ),
    ONE_ONE(
        labelResName = "1:1",
        ratio = 1f
    ),
    FOUR_FIVE(
        labelResName = "4:5",
        ratio = 4f / 5f
    ),
    THREE_FOUR(
        labelResName = "3:4",
        ratio = 3f / 4f
    ),
    SIXTEEN_NINE(
        labelResName = "16:9",
        ratio = 16f / 9f
    ),
    NINE_SIXTEEN(
        labelResName = "9:16",
        ratio = 9f / 16f
    )
}
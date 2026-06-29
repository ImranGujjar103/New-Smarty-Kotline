package com.imr.example.newsmartykotlin.domain.model

import com.imr.example.newsmartykotlin.R

enum class CropAspectRatio(
    val labelResName: String,
    val ratio: Float?,
    val iconResId : Int?
) {
    FREE(
        labelResName = "Free",
        ratio = null,
        iconResId = R.drawable.ic_crop_free
    ),
    ORIGINAL(
        labelResName = "Original",
        ratio = null,
        iconResId = R.drawable.ic_crop_original
    ),
    ONE_ONE(
        labelResName = "1:1",
        ratio = 1f,
        iconResId = R.drawable.ic_crop_one_one
    ),
    FOUR_FIVE(
        labelResName = "4:5",
        ratio = 4f / 5f,
        iconResId = R.drawable.ic_crop_four_five
    ),
    THREE_FOUR(
        labelResName = "3:4",
        ratio = 3f / 4f,
        iconResId = R.drawable.ic_crop_three_four
    ),
    SIXTEEN_NINE(
        labelResName = "16:9",
        ratio = 16f / 9f,
        iconResId = R.drawable.ic_crop_nine_sixteen
    ),
    NINE_SIXTEEN(
        labelResName = "9:16",
        ratio = 9f / 16f,
        iconResId = R.drawable.ic_crop_sixteen_nine
    )
}
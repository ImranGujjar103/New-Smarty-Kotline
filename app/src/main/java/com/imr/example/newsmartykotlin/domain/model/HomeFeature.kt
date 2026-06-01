package com.imr.example.newsmartykotlin.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomeFeature(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val imageRes: Int
)
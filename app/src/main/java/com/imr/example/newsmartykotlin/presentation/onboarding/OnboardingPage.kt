package com.imr.example.newsmartykotlin.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.imr.example.newsmartykotlin.R

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int = 0
)

val onboardingPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.img_ob1,
        titleRes = R.string.change_outfits_in_one_tap,
    ),
    OnboardingPage(
        imageRes = R.drawable.img_ob2,
        titleRes = R.string.make_passport_size,
        descRes = R.string.photos
    ),
    OnboardingPage(
        imageRes = R.drawable.img_ob3,
        titleRes = R.string.change_backgrounds,
        descRes = R.string.beach_photo_while_staying_at_home
    )
)
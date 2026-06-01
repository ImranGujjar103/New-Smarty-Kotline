package com.imr.example.newsmartykotlin.data.repository

import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.HomeFeature
import com.imr.example.newsmartykotlin.domain.repository.HomeRepository

class HomeRepositoryImpl : HomeRepository {

    override fun getHomeFeatures(): List<HomeFeature> {
        return listOf(
            HomeFeature(
                id = "face_swap",
                titleRes = R.string.face_swap,
                imageRes = R.drawable.img_face_swap
            ),
            HomeFeature(
                id = "passport_pic",
                titleRes = R.string.passport_pic,
                imageRes = R.drawable.img_passport_pic
            ),
            HomeFeature(
                id = "bg_changer",
                titleRes = R.string.bg_changer,
                imageRes = R.drawable.img_bg_changer
            ),
            HomeFeature(
                id = "my_creation",
                titleRes = R.string.my_creation,
                imageRes = R.drawable.img_my_creation
            )
        )
    }
}
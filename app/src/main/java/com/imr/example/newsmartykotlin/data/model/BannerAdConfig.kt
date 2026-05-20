package com.imr.example.newsmartykotlin.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.imr.example.newsmartykotlin.BuildConfig

@Keep
data class BannerAdConfig(
    @SerializedName("toShow") var toShow: Boolean = true,
    @SerializedName("ADID") val adId: String = if (BuildConfig.DEBUG){"ca-app-pub-3940256099942544/9214589741"} else{ ""}
)
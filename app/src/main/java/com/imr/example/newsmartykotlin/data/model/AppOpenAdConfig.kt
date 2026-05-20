package com.imr.example.newsmartykotlin.data.model

import androidx.annotation.Keep
import com.imr.example.newsmartykotlin.BuildConfig
import com.google.gson.annotations.SerializedName

@Keep
data class AppOpenAdConfig(
    @SerializedName("toShow") val toShow: Boolean = true,
    @SerializedName("ADID") val adId: String =if (BuildConfig.DEBUG){"ca-app-pub-3940256099942544/9257395921"} else{  ""}
)
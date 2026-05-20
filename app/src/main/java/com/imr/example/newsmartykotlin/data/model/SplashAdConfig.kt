package com.imr.example.newsmartykotlin.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.imr.example.newsmartykotlin.BuildConfig

@Keep
data class SplashAdConfig(
    @SerializedName("toShow") val toShow: Boolean = true,
    @SerializedName("ad_type") val adType: Int = 0,
    @SerializedName("ad_type_info") val adTypeInfo: String = "0 = Inter,1 = app open, else = no ad",
    @SerializedName("inter_id") val interId: String =if (BuildConfig.DEBUG){"ca-app-pub-3940256099942544/1033173712"} else{  ""},
    @SerializedName("appopen_id") val appOpenId: String = if (BuildConfig.DEBUG){"ca-app-pub-3940256099942544/9257395921"} else{ ""}

)

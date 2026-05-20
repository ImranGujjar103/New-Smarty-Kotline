package com.imr.example.newsmartykotlin.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TimeDurationForCloseBtnConfig(
    @SerializedName("Info")
    val info: String="Duration in seconds",
    @SerializedName("duration") val duration: Long = 1
)
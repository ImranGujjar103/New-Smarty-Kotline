package com.imr.example.newsmartykotlin.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AppConfig(
    /* Premium flags */
    @SerializedName("premium_home_show") val premiumHomeShow: Boolean = true,
    @SerializedName("premium_feature") val premiumFeature: Boolean = true,
    @SerializedName("premium_after_splash_show") val premiumAfterSplashShow: Boolean = false,
    @SerializedName("premium_ad_failed_show") val premiumAdFailedShow: Boolean = true,
    @SerializedName("showOnboardingScreen") val showOnboardingScreen: Boolean = true,
    @SerializedName("splashDuration") val splashDuration: TimeDurationConfig = TimeDurationConfig(),//it's in seconds
    @SerializedName("premiumCloseBtnDuration") val premiumCloseBtnDuration: TimeDurationForCloseBtnConfig = TimeDurationForCloseBtnConfig(),//it's in seconds

    @SerializedName("show_premium_dialog") val showPremiumDialog: Boolean = false,
    @SerializedName("ctr_color") val ctrColor: String = "#1e9956",

    @SerializedName("splash_ad") val splashAd: SplashAdConfig = SplashAdConfig(),
    @SerializedName("splashProAd") val splashProAd: SplashProAdConfig = SplashProAdConfig(),

    @SerializedName("appopen_resume") val appOpenResume: AppOpenAdConfig = AppOpenAdConfig(),
    @SerializedName("language_interstitial") val languageInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("onboarding_interstitial") val onBoardingInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("home_interstitial") val homeInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("gallery_interstitial") val galleryInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("save_interstitial") val saveInterstitial: InterstitialAdConfig = InterstitialAdConfig(),


    @SerializedName("languageNative") val languageNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("onboarding_native1") val onboardingNative1: NativeAdConfig = NativeAdConfig(),

    @SerializedName("home_native") val homeNative: NativeAdConfig = NativeAdConfig(),
    
    @SerializedName("eraser_native") val eraserNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("gallery_native") val galleryNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("bg_remover_native") val bgRemoverNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("background_text_native") val backgroundTextNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("crop_face_native") val cropFaceNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("saved_native") val savedNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("my_creation_native") val myCreationNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("suit_native") val suitNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("bg_remover_editor_native") val bgRemoverEditorNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("passport_country_native") val passportCountryNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("passport_detail_native") val passportDetailNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("settings_native") val settingsNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("photo_editor_native") val photoEditorNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("passport_cropper_native") val passportCropperNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("passport_background_native") val passportBackgroundNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("gallery_permission_native") val galleryPermissionNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("passport_result_native") val passportResultNative: NativeAdConfig = NativeAdConfig(),
    
    @SerializedName("splash_banner") val splashBanner: BannerAdConfig = BannerAdConfig(),


    )
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
    @SerializedName("splashDuration") val splashDuration: TimeDurationConfig = TimeDurationConfig(),//its in seconds
    @SerializedName("premiumCloseBtnDuration") val premiumCloseBtnDuration: TimeDurationForCloseBtnConfig = TimeDurationForCloseBtnConfig(),//its in seconds

    @SerializedName("show_premium_dialog") val showPremiumDialog: Boolean = false,
    @SerializedName("ctr_color") val ctrColor: String = "#1e9956",

    @SerializedName("splash_ad") val splashAd: SplashAdConfig = SplashAdConfig(),
    @SerializedName("splashProAd") val splashProAd: SplashProAdConfig = SplashProAdConfig(),

    @SerializedName("appopen_resume") val appOpenResume: AppOpenAdConfig = AppOpenAdConfig(),
    @SerializedName("language_interstitial") val languageInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("onboarding_interstitial") val onBoardingInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("home_interstitial") val homeInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("charging_interstitial") val chargingInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("clap_interstitial") val clapInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("handsfree_interstitial") val handsfreeInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("intruder_interstitial") val intruderInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("whistle_interstitial") val whistleInterstitial: InterstitialAdConfig = InterstitialAdConfig(),
    @SerializedName("Wifi_interstitial") val wifiInterstitial: InterstitialAdConfig = InterstitialAdConfig(),

    @SerializedName("languageNative") val languageNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("onboarding_native1") val onboardingNative1: NativeAdConfig = NativeAdConfig(),
    @SerializedName("onboarding_native2") val onboardingNative2: NativeAdConfig = NativeAdConfig(),
    @SerializedName("onboarding_native3") val onboardingNative3: NativeAdConfig = NativeAdConfig(),
    @SerializedName("onboarding_native4") val onboardingNative4: NativeAdConfig = NativeAdConfig(),
    @SerializedName("onboarding_native_fullscreen") val onboardingNativeFullscreen: NativeAdConfig = NativeAdConfig(),
    @SerializedName("home_native") val homeNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("exit_native") val exitNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("charging_native") val chargingNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("clap_native") val clapNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("handsfree_native") val handsfreeNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("intruder_native") val intruderNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("whistle_native") val whistleNative: NativeAdConfig = NativeAdConfig(),
    @SerializedName("wifi_native") val wifiNative: NativeAdConfig = NativeAdConfig(),

    @SerializedName("splash_banner") val splashBanner: BannerAdConfig = BannerAdConfig(),
    @SerializedName("home_banner") val homeBanner: BannerAdConfig = BannerAdConfig(),
    @SerializedName("charging_banner") val chargingBanner: BannerAdConfig = BannerAdConfig(),
    @SerializedName("clap_banner") val clapBanner: BannerAdConfig = BannerAdConfig(),
    @SerializedName("handsfree_banner") val handsfreeBanner: BannerAdConfig = BannerAdConfig(),
    @SerializedName("intruder_banner") val intruderBanner: BannerAdConfig = BannerAdConfig(),
    @SerializedName("whistle_banner") val whistleBanner: BannerAdConfig = BannerAdConfig(),
    @SerializedName("wifi_banner") val wifiBanner: BannerAdConfig = BannerAdConfig(),

    )
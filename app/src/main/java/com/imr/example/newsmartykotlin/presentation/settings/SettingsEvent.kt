package com.imr.example.newsmartykotlin.presentation.settings

sealed class SettingsEvent {
    data object NavigateBack : SettingsEvent()
    data object NavigateToLanguage : SettingsEvent()
    data object NavigateToPremium : SettingsEvent()
    data object ShareApp : SettingsEvent()
    data object RateApp : SettingsEvent()
    data object PrivacyPolicy : SettingsEvent()
}

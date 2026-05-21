package com.imr.example.newsmartykotlin.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.imr.example.newsmartykotlin.core.permission.AppPermissionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AntiTheft_DataStore")

class DataStorePrefs(private val context: Context) {

    private val PREMIUM_PURCHASED = booleanPreferencesKey("PREMIUM_PURCHASED_AntiTheft")
    private val IS_Consent = booleanPreferencesKey("IS_Consent")

    private val IS_FIRST_SPLASH = booleanPreferencesKey("IS_FIRST_SPLASH")

    // Premium Billing Keys
    private val IS_MONTHLY_TRIAL = booleanPreferencesKey("IS_MONTHLY_TRIAL")
    private val MONTHLY_TRIAL_INFO = stringPreferencesKey("MONTHLY_TRIAL_INFO")
    private val MONTHLY_TRIAL_PRICE = stringPreferencesKey("MONTHLY_TRIAL_PRICE")
    private val MONTHLY_TRIAL_INFO_AFTER = stringPreferencesKey("MONTHLY_TRIAL_INFO_AFTER")
    private val MONTHLY_PRICE = stringPreferencesKey("MONTHLY_PRICE")

    private val IS_YEARLY_TRIAL = booleanPreferencesKey("IS_YEARLY_TRIAL")
    private val YEARLY_TRIAL_INFO = stringPreferencesKey("YEARLY_TRIAL_INFO")
    private val YEARLY_TRIAL_PRICE = stringPreferencesKey("YEARLY_TRIAL_PRICE")
    private val YEARLY_TRIAL_INFO_AFTER = stringPreferencesKey("YEARLY_TRIAL_INFO_AFTER")
    private val YEARLY_PRICE = stringPreferencesKey("YEARLY_PRICE")

    private val PREMIUM_LAYOUT_TYPE = stringPreferencesKey("PREMIUM_LAYOUT_TYPE")
    private val SELECTED_LANGUAGE_CODE = stringPreferencesKey("SELECTED_LANGUAGE_CODE")
    private val SELECTED_LANGUAGE_DATA = stringPreferencesKey("SELECTED_LANGUAGE_DATA")
    private val LANGUAGE_SELECTED = booleanPreferencesKey("LANGUAGE_SELECTED")
    private val LANGUAGE_HAND_SHOWN = booleanPreferencesKey("LANGUAGE_HAND_SHOWN")
    private val SELECTED_RINGTONE_NAME = stringPreferencesKey("selected_ringtone_name")
    private val SELECTED_RINGTONE_URI = stringPreferencesKey("selected_ringtone_uri")
    private val SAVED_PIN = stringPreferencesKey("SAVED_PIN")
    private val PIN_ENABLED = booleanPreferencesKey("PIN_ENABLED")
    private val NOTIFICATION_DENIED_COUNT = intPreferencesKey("NOTIFICATION_DENIED_COUNT")
    private val MICROPHONE_DENIED_COUNT = intPreferencesKey("MICROPHONE_DENIED_COUNT")
    private val CAMERA_DENIED_COUNT = intPreferencesKey("CAMERA_DENIED_COUNT")
    private val STORAGE_DENIED_COUNT = intPreferencesKey("STORAGE_DENIED_COUNT")
    private val CLAP_ACTIVE = booleanPreferencesKey("CLAP_ACTIVE")
    private val WHISTLE_ACTIVE = booleanPreferencesKey("WHISTLE_ACTIVE")
    private val INTRUDER_ACTIVE = booleanPreferencesKey("INTRUDER_ACTIVE")
    private val CHARGING_ACTIVE = booleanPreferencesKey("CHARGING_ACTIVE")
    private val HANDSFREE_ACTIVE = booleanPreferencesKey("HANDSFREE_ACTIVE")
    private val WIFI_ACTIVE = booleanPreferencesKey("WIFI_ACTIVE")

    private val ACTIVE_FEATURE_KEY = stringPreferencesKey("ACTIVE_FEATURE_KEY")
    private val CLAP_FLASHLIGHT_ENABLED = booleanPreferencesKey("CLAP_FLASHLIGHT_ENABLED")
    private val CLAP_VIBRATION_ENABLED = booleanPreferencesKey("CLAP_VIBRATION_ENABLED")

    private val WHISTLE_FLASHLIGHT_ENABLED = booleanPreferencesKey("WHISTLE_FLASHLIGHT_ENABLED")
    private val WHISTLE_VIBRATION_ENABLED = booleanPreferencesKey("WHISTLE_VIBRATION_ENABLED")

    private val WIFI_FLASHLIGHT_ENABLED = booleanPreferencesKey("WIFI_FLASHLIGHT_ENABLED")
    private val WIFI_VIBRATION_ENABLED = booleanPreferencesKey("WIFI_VIBRATION_ENABLED")

    private val CHARGING_FLASHLIGHT_ENABLED =
        booleanPreferencesKey("CHARGING_FLASHLIGHT_ENABLED")

    private val CHARGING_VIBRATION_ENABLED =
        booleanPreferencesKey("CHARGING_VIBRATION_ENABLED")

    private val CHARGING_REMOVED_ENABLED =
        booleanPreferencesKey("CHARGING_REMOVED_ENABLED")

    private val CHARGING_FULL_ENABLED =
        booleanPreferencesKey("CHARGING_FULL_ENABLED")

    private val HANDSFREE_FLASHLIGHT_ENABLED =
        booleanPreferencesKey("HANDSFREE_FLASHLIGHT_ENABLED")

    private val HANDSFREE_VIBRATION_ENABLED =
        booleanPreferencesKey("HANDSFREE_VIBRATION_ENABLED")

    private val INTRUDER_SELFIE_ENABLED =
        booleanPreferencesKey("INTRUDER_SELFIE_ENABLED")



    private val MOTION_FLASHLIGHT_ENABLED =
        booleanPreferencesKey("MOTION_FLASHLIGHT_ENABLED")

    private val MOTION_VIBRATION_ENABLED =
        booleanPreferencesKey("MOTION_VIBRATION_ENABLED")

    private val MOTION_GRACE_TIME_BEFORE =
        longPreferencesKey("MOTION_GRACE_TIME_BEFORE")

    private val MOTION_GRACE_TIME_AFTER =
        longPreferencesKey("MOTION_GRACE_TIME_AFTER")

    suspend fun setMotionFlashlightEnabled(value: Boolean) {
        setBoolean(MOTION_FLASHLIGHT_ENABLED, value)
    }

    fun getMotionFlashlightEnabled(): Flow<Boolean> {
        return getBoolean(MOTION_FLASHLIGHT_ENABLED, true)
    }

    suspend fun setMotionVibrationEnabled(value: Boolean) {
        setBoolean(MOTION_VIBRATION_ENABLED, value)
    }

    fun getMotionVibrationEnabled(): Flow<Boolean> {
        return getBoolean(MOTION_VIBRATION_ENABLED, true)
    }

    suspend fun setMotionGraceTimeBefore(value: Long) {
        setLong(MOTION_GRACE_TIME_BEFORE, value)
    }

    fun getMotionGraceTimeBefore(): Flow<Long> {
        return getLong(MOTION_GRACE_TIME_BEFORE, 1000L)
    }

    suspend fun setMotionGraceTimeAfter(value: Long) {
        setLong(MOTION_GRACE_TIME_AFTER, value)
    }

    fun getMotionGraceTimeAfter(): Flow<Long> {
        return getLong(MOTION_GRACE_TIME_AFTER, 1500L)
    }
    suspend fun setIntruderSelfieEnabled(value: Boolean) {
        setBoolean(INTRUDER_SELFIE_ENABLED, value)
    }

    fun getIntruderSelfieEnabled(): Flow<Boolean> {
        return getBoolean(INTRUDER_SELFIE_ENABLED, false)
    }

    suspend fun setHandsfreeFlashlightEnabled(value: Boolean) {
        setBoolean(HANDSFREE_FLASHLIGHT_ENABLED, value)
    }

    fun getHandsfreeFlashlightEnabled(): Flow<Boolean> {
        return getBoolean(HANDSFREE_FLASHLIGHT_ENABLED, true)
    }

    suspend fun setHandsfreeVibrationEnabled(value: Boolean) {
        setBoolean(HANDSFREE_VIBRATION_ENABLED, value)
    }

    fun getHandsfreeVibrationEnabled(): Flow<Boolean> {
        return getBoolean(HANDSFREE_VIBRATION_ENABLED, true)
    }

    suspend fun setChargingFlashlightEnabled(value: Boolean) {
        setBoolean(CHARGING_FLASHLIGHT_ENABLED, value)
    }

    fun getChargingFlashlightEnabled(): Flow<Boolean> {
        return getBoolean(CHARGING_FLASHLIGHT_ENABLED, true)
    }

    suspend fun setChargingVibrationEnabled(value: Boolean) {
        setBoolean(CHARGING_VIBRATION_ENABLED, value)
    }

    fun getChargingVibrationEnabled(): Flow<Boolean> {
        return getBoolean(CHARGING_VIBRATION_ENABLED, true)
    }

    suspend fun setChargingRemovedEnabled(value: Boolean) {
        setBoolean(CHARGING_REMOVED_ENABLED, value)
    }

    fun getChargingRemovedEnabled(): Flow<Boolean> {
        return getBoolean(CHARGING_REMOVED_ENABLED, true)
    }

    suspend fun setChargingFullEnabled(value: Boolean) {
        setBoolean(CHARGING_FULL_ENABLED, value)
    }

    fun getChargingFullEnabled(): Flow<Boolean> {
        return getBoolean(CHARGING_FULL_ENABLED, false)
    }

    suspend fun setWifiFlashlightEnabled(value: Boolean) {
        setBoolean(WIFI_FLASHLIGHT_ENABLED, value)
    }

    fun getWifiFlashlightEnabled(): Flow<Boolean> {
        return getBoolean(WIFI_FLASHLIGHT_ENABLED, true)
    }

    suspend fun setWifiVibrationEnabled(value: Boolean) {
        setBoolean(WIFI_VIBRATION_ENABLED, value)
    }

    fun getWifiVibrationEnabled(): Flow<Boolean> {
        return getBoolean(WIFI_VIBRATION_ENABLED, true)
    }

    suspend fun setWhistleFlashlightEnabled(value: Boolean) {
        setBoolean(WHISTLE_FLASHLIGHT_ENABLED, value)
    }

    fun getWhistleFlashlightEnabled(): Flow<Boolean> {
        return getBoolean(WHISTLE_FLASHLIGHT_ENABLED, false)
    }

    suspend fun setWhistleVibrationEnabled(value: Boolean) {
        setBoolean(WHISTLE_VIBRATION_ENABLED, value)
    }

    fun getWhistleVibrationEnabled(): Flow<Boolean> {
        return getBoolean(WHISTLE_VIBRATION_ENABLED, true)
    }

    suspend fun setClapFlashlightEnabled(value: Boolean) {
        setBoolean(CLAP_FLASHLIGHT_ENABLED, value)
    }

    fun getClapFlashlightEnabled(): Flow<Boolean> {
        return getBoolean(CLAP_FLASHLIGHT_ENABLED, true)
    }

    suspend fun setClapVibrationEnabled(value: Boolean) {
        setBoolean(CLAP_VIBRATION_ENABLED, value)
    }

    fun getClapVibrationEnabled(): Flow<Boolean> {
        return getBoolean(CLAP_VIBRATION_ENABLED, true)
    }

    fun getActiveFeatureKey(): Flow<String> {
        return getString(ACTIVE_FEATURE_KEY, "")
    }

    suspend fun setActiveFeatureKey(value: String) {
        setString(ACTIVE_FEATURE_KEY, value)
    }

    suspend fun clearActiveFeatureKey() {
        setString(ACTIVE_FEATURE_KEY, "")
    }
    // SETTERS
    private suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { it[key] = value }
    }

    private suspend fun setString(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { it[key] = value }
    }

    private suspend fun setInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { it[key] = value }
    }

    private suspend fun setLong(key: Preferences.Key<Long>, value: Long) {
        context.dataStore.edit { it[key] = value }
    }

    // GETTERS (Flow based)
    private fun getBoolean(key: Preferences.Key<Boolean>, default: Boolean = false): Flow<Boolean> {
        return context.dataStore.data.map { it[key] ?: default }
    }

    fun getString(key: Preferences.Key<String>, default: String = ""): Flow<String> {
        return context.dataStore.data.map { it[key] ?: default }
    }

    private fun getInt(key: Preferences.Key<Int>, default: Int = 0): Flow<Int> {
        return context.dataStore.data.map { it[key] ?: default }
    }

    private fun getLong(key: Preferences.Key<Long>, default: Long = 0L): Flow<Long> {
        return context.dataStore.data.map { it[key] ?: default }
    }



  /*  private fun getFeatureActiveKey(key: FeatureKey): Preferences.Key<Boolean> {
        return when (key) {
            FeatureKey.CLAP -> CLAP_ACTIVE
            FeatureKey.WHISTLE -> WHISTLE_ACTIVE
            FeatureKey.INTRUDER_SELFIE -> INTRUDER_ACTIVE
            FeatureKey.CHARGING -> CHARGING_ACTIVE
            FeatureKey.HANDSFREE -> HANDSFREE_ACTIVE
            FeatureKey.WIFI -> WIFI_ACTIVE
            FeatureKey.MOTION -> MOTION_FLASHLIGHT_ENABLED
        }
    }*/

/*    fun isFeatureActive(key: FeatureKey): Flow<Boolean> {
        return getBoolean(getFeatureActiveKey(key), false)
    }

    suspend fun setFeatureActive(key: FeatureKey, value: Boolean) {
        setBoolean(getFeatureActiveKey(key), value)
    }*/

    private fun deniedCountKey(type: AppPermissionType): Preferences.Key<Int> {
        return when (type) {
            AppPermissionType.NOTIFICATION -> NOTIFICATION_DENIED_COUNT
            AppPermissionType.MICROPHONE -> MICROPHONE_DENIED_COUNT
            AppPermissionType.CAMERA -> CAMERA_DENIED_COUNT
            AppPermissionType.STORAGE -> STORAGE_DENIED_COUNT
        }
    }

    fun getPermissionDeniedCount(type: AppPermissionType): Flow<Int> {
        return getInt(deniedCountKey(type), 0)
    }

    suspend fun increasePermissionDeniedCount(type: AppPermissionType) {
        val key = deniedCountKey(type)
        val current = getPermissionDeniedCount(type).first()
        setInt(key, current + 1)
    }

    suspend fun resetPermissionDeniedCount(type: AppPermissionType) {
        setInt(deniedCountKey(type), 0)
    }

    suspend fun savePin(pin: String) {
        setString(SAVED_PIN, pin)
    }

    fun getSavedPin(): Flow<String> {
        return getString(SAVED_PIN, "")
    }

    suspend fun setPinEnabled(value: Boolean) {
        setBoolean(PIN_ENABLED, value)
    }

    fun getPinEnabled(): Flow<Boolean> {
        return getBoolean(PIN_ENABLED, false)
    }

    suspend fun clearPin() {
        context.dataStore.edit {
            it.remove(SAVED_PIN)
            it[PIN_ENABLED] = false
        }
    }


    suspend fun saveSelectedRingtoneName(value: String) {
        setString(SELECTED_RINGTONE_NAME, value)
    }

    fun getSelectedRingtoneName(): Flow<String> {
        return getString(SELECTED_RINGTONE_NAME, "Fire")
    }

    suspend fun saveSelectedRingtoneUri(value: String) {
        setString(SELECTED_RINGTONE_URI, value)
    }

    fun getSelectedRingtoneUri(): Flow<String> {
        return getString(SELECTED_RINGTONE_URI, "")
    }

    suspend fun setSelectedLanguageCode(value: String) = setString(SELECTED_LANGUAGE_CODE, value)
    fun getSelectedLanguageCode(): Flow<String> = getString(SELECTED_LANGUAGE_CODE, "")

    suspend fun setSelectedLanguageData(value: String) = setString(SELECTED_LANGUAGE_DATA, value)
    fun getSelectedLanguageData(): Flow<String> = getString(SELECTED_LANGUAGE_DATA, "")

    suspend fun setLanguageSelected(value: Boolean) = setBoolean(LANGUAGE_SELECTED, value)
    fun getLanguageSelected(): Flow<Boolean> = getBoolean(LANGUAGE_SELECTED, false)

    suspend fun setLanguageHandShown(value: Boolean) = setBoolean(LANGUAGE_HAND_SHOWN, value)
    fun getLanguageHandShown(): Flow<Boolean> = getBoolean(LANGUAGE_HAND_SHOWN, false)
    // General Preferences
    suspend fun setIsConsent(value: Boolean) = setBoolean(IS_Consent, value)
    fun getIsConsent(): Flow<Boolean> = getBoolean(IS_Consent, false)

    suspend fun isFirstSplash(value: Boolean) = setBoolean(IS_FIRST_SPLASH, value)
    fun isFirstSplash(): Flow<Boolean> = getBoolean(IS_FIRST_SPLASH, true)

    suspend fun setIsPurchased(value: Boolean) = setBoolean(PREMIUM_PURCHASED, value)
    fun getIsPurchased(): Flow<Boolean> = getBoolean(PREMIUM_PURCHASED, true)

    // Monthly Premium Preferences
    suspend fun setIsMonthlyTrial(value: Boolean) = setBoolean(IS_MONTHLY_TRIAL, value)
    fun getIsMonthlyTrial(): Flow<Boolean> = getBoolean(IS_MONTHLY_TRIAL, false)

    suspend fun setMonthlyTrialInfo(value: String) = setString(MONTHLY_TRIAL_INFO, value)
    fun getMonthlyTrialInfo(): Flow<String> = getString(MONTHLY_TRIAL_INFO, "")

    suspend fun setMonthlyTrialPrice(value: String) = setString(MONTHLY_TRIAL_PRICE, value)
    fun getMonthlyTrialPrice(): Flow<String> = getString(MONTHLY_TRIAL_PRICE, "")

    suspend fun setMonthlyTrialInfoAfter(value: String) = setString(MONTHLY_TRIAL_INFO_AFTER, value)
    fun getMonthlyTrialInfoAfter(): Flow<String> = getString(MONTHLY_TRIAL_INFO_AFTER, "")

    suspend fun setMonthlyPrice(value: String) = setString(MONTHLY_PRICE, value)
    fun getMonthlyPrice(): Flow<String> = getString(MONTHLY_PRICE, "")


    // Yearly Premium Preferences
    suspend fun setIsYearlyTrial(value: Boolean) = setBoolean(IS_YEARLY_TRIAL, value)
    fun getIsYearlyTrial(): Flow<Boolean> = getBoolean(IS_YEARLY_TRIAL, false)

    suspend fun setYearlyTrialInfo(value: String) = setString(YEARLY_TRIAL_INFO, value)
    fun getYearlyTrialInfo(): Flow<String> = getString(YEARLY_TRIAL_INFO, "")

    suspend fun setYearlyTrialPrice(value: String) = setString(YEARLY_TRIAL_PRICE, value)
    fun getYearlyTrialPrice(): Flow<String> = getString(YEARLY_TRIAL_PRICE, "")

    suspend fun setYearlyTrialInfoAfter(value: String) = setString(YEARLY_TRIAL_INFO_AFTER, value)
    fun getYearlyTrialInfoAfter(): Flow<String> = getString(YEARLY_TRIAL_INFO_AFTER, "")

    suspend fun setYearlyPrice(value: String) = setString(YEARLY_PRICE, value)
    fun getYearlyPrice(): Flow<String> = getString(YEARLY_PRICE, "")


    // Remote Config Premium Layout
    suspend fun setPremiumLayoutType(value: String) = setString(PREMIUM_LAYOUT_TYPE, value)
    fun getPremiumLayoutType(): Flow<String> = getString(PREMIUM_LAYOUT_TYPE, "two_card")
    // Monthly Paid Trial Preferences

    // Clear all data
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

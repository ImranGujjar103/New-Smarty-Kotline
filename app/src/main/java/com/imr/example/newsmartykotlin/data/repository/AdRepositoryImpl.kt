package com.imr.example.newsmartykotlin.data.repository

import android.util.Log

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.imr.example.newsmartykotlin.BuildConfig
import com.imr.example.newsmartykotlin.MyApp
import com.imr.example.newsmartykotlin.core.ads.AdManager
import com.imr.example.newsmartykotlin.data.model.AppConfig
import com.imr.example.newsmartykotlin.domain.repository.AdRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.jvm.java

class AdRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig
) : AdRepository {

    private val _appConfig = MutableStateFlow(AppConfig())
    override val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    companion object {
        private const val TAG = "AdRepositoryImpl"
    }

    /**
     * Fetch remote configuration values with Result wrapper for better error handling
     */
    override suspend fun fetchRemoteConfig(): Result<AppConfig> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                        if (!continuation.isActive) return@addOnCompleteListener

                        if (task.isSuccessful) {
                            Log.d(TAG, "fetchRemoteConfig: task.isSuccessful")

                            // Parse config on the current thread (IO thread if resumed properly,
                            // but listener might be on Main. Gson is small here, but let's be safe).
                            try {
                                adSettingsSynced = true
                                val adData = if (BuildConfig.DEBUG) {
                                    remoteConfig.getValue(AdManager.AD_SETTING_DEBUG)
                                } else {
                                    remoteConfig.getValue(AdManager.AD_SETTING_RELEASE)
                                }

                                val json = adData.asString()
                                Log.d(TAG, "config-data is $json")

                                if (json.isEmpty()) {
                                    continuation.resume(Result.success(AppConfig()))
                                    return@addOnCompleteListener
                                }

                                val adSettings = Gson().fromJson(json, AppConfig::class.java)
                                MyApp.mInstance?.appConfig = adSettings
                                MyApp.mInstance?.ctrBTNColor = adSettings.ctrColor
                                _appConfig.value = adSettings

                                continuation.resume(Result.success(adSettings))
                            } catch (e: Exception) {
                                Log.e(TAG, "Parsing failed", e)
                                continuation.resume(Result.success(AppConfig())) // Return default on parse error
                            }
                        } else {
                            Log.e(TAG, "Fetch failed", task.exception)
                            continuation.resume(Result.failure(task.exception ?: Exception("Fetch failed")))
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "fetchRemoteConfig catch", e)
                    continuation.resume(Result.failure(e))
                }
            }
        }
    override var adSettingsSynced: Boolean = false
}

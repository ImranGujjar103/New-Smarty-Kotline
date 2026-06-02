package com.imr.example.newsmartykotlin.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PermissionDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val PERMISSION_DENY_COUNT_KEY = intPreferencesKey("permission_deny_count")
    }

    fun getDenyCount(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[PERMISSION_DENY_COUNT_KEY] ?: 0
        }
    }

    suspend fun incrementDenyCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[PERMISSION_DENY_COUNT_KEY] ?: 0
            preferences[PERMISSION_DENY_COUNT_KEY] = currentCount + 1
        }
    }

    suspend fun resetDenyCount() {
        dataStore.edit { preferences ->
            preferences[PERMISSION_DENY_COUNT_KEY] = 0
        }
    }
}
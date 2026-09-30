package com.example.plantgrid.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserSettings(
    val language: String,
    val alertsEnabled: Boolean,
    val syncIntervalMinutes: Int,
    val brokerUrl: String,
    val useMetric: Boolean,
    val selectedCrop: String = "Haladi",
    val isOnboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val userPhone: String = "",
    val userRole: String = "Farmer"
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val ALERTS_ENABLED = booleanPreferencesKey("alerts_enabled")
        val SYNC_INTERVAL = intPreferencesKey("sync_interval")
        val BROKER_URL = stringPreferencesKey("broker_url")
        val USE_METRIC = booleanPreferencesKey("use_metric")
        val SELECTED_CROP = stringPreferencesKey("selected_crop")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    val userSettings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            language = preferences[Keys.LANGUAGE] ?: "en",
            alertsEnabled = preferences[Keys.ALERTS_ENABLED] ?: true,
            syncIntervalMinutes = preferences[Keys.SYNC_INTERVAL] ?: 15,
            brokerUrl = preferences[Keys.BROKER_URL] ?: "ws://192.168.1.102:8080/telemetry",
            useMetric = preferences[Keys.USE_METRIC] ?: true,
            selectedCrop = preferences[Keys.SELECTED_CROP] ?: "Haladi",
            isOnboardingCompleted = preferences[Keys.ONBOARDING_COMPLETED] ?: false,
            isLoggedIn = preferences[Keys.IS_LOGGED_IN] ?: false,
            userName = preferences[Keys.USER_NAME] ?: "",
            userPhone = preferences[Keys.USER_PHONE] ?: "",
            userRole = preferences[Keys.USER_ROLE] ?: "Farmer"
        )
    }

    suspend fun updateUserProfile(name: String, phone: String, role: String, loggedIn: Boolean) {
        context.dataStore.edit {
            it[Keys.USER_NAME] = name
            it[Keys.USER_PHONE] = phone
            it[Keys.USER_ROLE] = role
            it[Keys.IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun updateSelectedCrop(crop: String) {
        context.dataStore.edit { it[Keys.SELECTED_CROP] = crop }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun updateAlerts(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ALERTS_ENABLED] = enabled }
    }

    suspend fun updateSyncInterval(minutes: Int) {
        context.dataStore.edit { it[Keys.SYNC_INTERVAL] = minutes }
    }

    suspend fun updateBrokerUrl(url: String) {
        context.dataStore.edit { it[Keys.BROKER_URL] = url }
    }

    suspend fun updateMeasurementSystem(useMetric: Boolean) {
        context.dataStore.edit { it[Keys.USE_METRIC] = useMetric }
    }
}

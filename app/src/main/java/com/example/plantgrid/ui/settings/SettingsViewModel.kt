package com.example.plantgrid.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrid.data.settings.SettingsRepository
import com.example.plantgrid.data.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<UserSettings> = repository.userSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings("en", true, 15, "ws://192.168.1.102:8080/telemetry", true)
    )

    fun setLanguage(lang: String) = viewModelScope.launch { repository.updateLanguage(lang) }
    fun setAlerts(enabled: Boolean) = viewModelScope.launch { repository.updateAlerts(enabled) }
    fun setSyncInterval(min: Int) = viewModelScope.launch { repository.updateSyncInterval(min) }
    fun setBrokerUrl(url: String) = viewModelScope.launch { repository.updateBrokerUrl(url) }
    fun setMeasurementSystem(useMetric: Boolean) = viewModelScope.launch { repository.updateMeasurementSystem(useMetric) }
    fun setSelectedCrop(crop: String) = viewModelScope.launch { repository.updateSelectedCrop(crop) }
    fun setOnboardingCompleted(completed: Boolean) = viewModelScope.launch { repository.updateOnboardingCompleted(completed) }
    fun saveUserProfile(name: String, phone: String, role: String, loggedIn: Boolean) = viewModelScope.launch {
        repository.updateUserProfile(name, phone, role, loggedIn)
    }
}

package com.otorresd.ram.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otorresd.ram.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject internal constructor(private val manager: DataStoreManager): ViewModel() {
    val isDarkMode = manager.isLightMode

    fun setDarkMode(activateDarkMode: Boolean){
        viewModelScope.launch {
            manager.saveThemeMode(activateDarkMode)
        }
    }
}
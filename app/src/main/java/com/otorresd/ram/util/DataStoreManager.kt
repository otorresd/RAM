package com.otorresd.ram.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager(private val context: Context) {
    object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("DARK_MODE")
    }

    val isLightMode: Flow<Boolean> = context.dataStore.data
        .map { currentPreferences ->
            // Unlike Proto DataStore, there's no type safety here.
            currentPreferences[PreferencesKeys.DARK_MODE] ?: true
        }

    suspend fun saveThemeMode(darkMode: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.DARK_MODE] = darkMode
        }
    }
}
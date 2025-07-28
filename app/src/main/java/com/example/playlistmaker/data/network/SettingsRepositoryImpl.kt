package com.example.playlistmaker.data.network

import android.content.SharedPreferences
import com.example.playlistmaker.THEME_BOOLEAN_KEY
import com.example.playlistmaker.domain.api.SettingsRepository
import androidx.core.content.edit

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : SettingsRepository {

    override fun isDarkTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_BOOLEAN_KEY, false)
    }

    override fun saveThemeSetting(isDarkTheme: Boolean) {
        sharedPrefs.edit { putBoolean(THEME_BOOLEAN_KEY, isDarkTheme) }
    }
}
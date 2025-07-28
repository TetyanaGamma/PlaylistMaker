package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.creator.THEME_BOOLEAN_KEY
import com.example.playlistmaker.settings.domain.SettingsRepository

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
package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.creator.THEME_BOOLEAN_KEY
import com.example.playlistmaker.settings.data.storage.ThemeStorageClient
import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(
    private val themeStorage: ThemeStorageClient
) : SettingsRepository {

    override fun isDarkTheme(): Boolean {
        return themeStorage.getData()
    }

    override fun saveThemeSetting(isDarkTheme: Boolean) {
        themeStorage.storeData(isDarkTheme)
    }
}
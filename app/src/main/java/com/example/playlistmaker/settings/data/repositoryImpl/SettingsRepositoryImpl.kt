package com.example.playlistmaker.settings.data.repositoryImpl

import com.example.playlistmaker.settings.data.storage.ThemeStorageClient
import com.example.playlistmaker.settings.domain.api.SettingsRepository

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
package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun isDarkTheme(): Boolean {
        return settingsRepository.isDarkTheme()
    }

    override fun switchTheme(isDarkTheme: Boolean) {
        settingsRepository.saveThemeSetting(isDarkTheme)
    }
}
package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.api.SettingsRepository

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
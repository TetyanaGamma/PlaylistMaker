package com.example.playlistmaker.settings.domain.interactor

interface SettingsInteractor {

    fun isDarkTheme(): Boolean
    fun switchTheme(isDarkTheme: Boolean)
}
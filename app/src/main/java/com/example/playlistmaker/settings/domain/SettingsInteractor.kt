package com.example.playlistmaker.settings.domain

interface SettingsInteractor {

    fun isDarkTheme(): Boolean
    fun switchTheme(isDarkTheme: Boolean)
}
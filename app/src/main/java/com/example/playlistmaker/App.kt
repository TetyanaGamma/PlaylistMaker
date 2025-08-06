package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.di.playerModule
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    var darkTheme: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()

        // Инициализация интерактора настроек
        settingsInteractor = Creator.provideSettingsInteractor(this)

        // Получаем текущую тему из интерактора
        darkTheme = settingsInteractor.isDarkTheme()
        applyTheme(darkTheme)

        startKoin {
            androidContext(this@App)
            modules(playerModule)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        // Сохраняем настройку через интерактор
        settingsInteractor.switchTheme(darkThemeEnabled)

        applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
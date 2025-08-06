package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.data.storage.ThemeStorageClient
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.sharing.data.repositoryImpl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> {
        object : SettingsRepository {
            private val themeStorage = ThemeStorageClient(get())
            override fun isDarkTheme() = themeStorage.getData()
            override fun saveThemeSetting(isDarkTheme: Boolean) {
                themeStorage.storeData(isDarkTheme)
            }
        }
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }
}
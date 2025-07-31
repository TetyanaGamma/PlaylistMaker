package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SupportData

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val isDarkTheme = MutableLiveData(settingsInteractor.isDarkTheme())
    fun observe(): LiveData<Boolean> = isDarkTheme

    companion object{
        fun getFactory(settingsInteractor: SettingsInteractor,
                       sharingInteractor: SharingInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(settingsInteractor, sharingInteractor)
            }
        }
    }

    fun toggleTheme(enabled: Boolean) {
        settingsInteractor.switchTheme(enabled)
        isDarkTheme.value = enabled
    }

    fun getShareLink(): String = sharingInteractor.getShareAppLink()

    fun getSupportData(): SupportData = sharingInteractor.getSupportEmailData()

    fun getUserAgreementUrl(): String = sharingInteractor.getUserAgreementUrl()

}
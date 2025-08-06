package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SupportData

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val isDarkTheme = MutableLiveData(settingsInteractor.isDarkTheme())
    fun observe(): LiveData<Boolean> = isDarkTheme


    fun toggleTheme(enabled: Boolean) {
        settingsInteractor.switchTheme(enabled)
        isDarkTheme.value = enabled
    }

    fun getShareLink(): String = sharingInteractor.getShareAppLink()

    fun getSupportData(): SupportData = sharingInteractor.getSupportEmailData()

    fun getUserAgreementUrl(): String = sharingInteractor.getUserAgreementUrl()


}
package com.example.playlistmaker.sharing.domain

interface SharingInteractor {
    fun getShareAppLink(): String
    fun getSupportEmailData(): SupportData
    fun getUserAgreementUrl(): String
}
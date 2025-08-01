package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.sharing.domain.SupportData

interface SharingInteractor {
    fun getShareAppLink(): String
    fun getSupportEmailData(): SupportData
    fun getUserAgreementUrl(): String
}
package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SupportData

class SharingInteractorImpl(
    private val context: Context
) : SharingInteractor {

    override fun getShareAppLink(): String {
        return context.getString(R.string.massage_to_share)
    }

    override fun getSupportEmailData(): SupportData {
        return SupportData(
            email = context.getString(R.string.my_email),
            subject = context.getString(R.string.email_subject),
            message = context.getString(R.string.email_body)
        )
    }

    override fun getUserAgreementUrl(): String {
        return context.getString(R.string.user_agreement_url)
    }
}
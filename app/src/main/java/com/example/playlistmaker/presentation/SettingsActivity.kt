package com.example.playlistmaker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.THEME_BOOLEAN_KEY
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsInteractor = Creator.provideSettingsInteractor(this)


        val toolbar: Toolbar = findViewById(R.id.settings_toolbar)
        //обрабатываем нажатие на стрелку назад и возвращаемся на главный экран
        toolbar.setNavigationOnClickListener {
            finish()
        }
        setupShareButton()
        setupSupportButton()
        setupUserAgreementButton()
        setupThemeSwitch()


        }

    private fun setupShareButton() {
        findViewById<TextView>(R.id.settings_share).setOnClickListener {
            val messageToShare = getString(R.string.massage_to_share)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, messageToShare)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }
    private fun setupSupportButton() {
        findViewById<TextView>(R.id.settings_support).setOnClickListener {
            val email = getString(R.string.my_email)
            val emailSubject = getString(R.string.email_subject)
            val emailBody = getString(R.string.email_body)
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                putExtra(Intent.EXTRA_TEXT, emailBody)
            }
            startActivity(supportIntent)
        }
    }


    private fun setupUserAgreementButton() {
        findViewById<TextView>(R.id.settings_user_agreement).setOnClickListener {
            val agreementUrl = getString(R.string.user_agreement_url)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(agreementIntent)
        }
    }

    private fun setupThemeSwitch() {
        val themeSwitch = findViewById<SwitchMaterial>(R.id.settings_switch_theme)
        themeSwitch.isChecked = settingsInteractor.isDarkTheme()

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractor.switchTheme(isChecked)
            (applicationContext as App).switchTheme(isChecked)
        }
    }

}
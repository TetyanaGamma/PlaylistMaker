package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var themeSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(
                Creator.provideSettingsInteractor(this),
                Creator.provideSharingInteractor(this)
            )
        )[SettingsViewModel::class.java]


        val toolbar: Toolbar = findViewById(R.id.settings_toolbar)
        //обрабатываем нажатие на стрелку назад и возвращаемся на главный экран
        toolbar.setNavigationOnClickListener {
            finish()
        }
        themeSwitch  = findViewById(R.id.settings_switch_theme)

        setupThemeObserver()
        setupThemeSwitch()
        setupShareButton()
        setupSupportButton()
        setupUserAgreementButton()

    }

    private fun setupThemeObserver() {
        viewModel.observe().observe(this) { isDarkTheme ->
            themeSwitch.isChecked = isDarkTheme
            (application as App).switchTheme(isDarkTheme)
        }
    }

    private fun setupThemeSwitch() {
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }
    }

    private fun setupShareButton() {
        findViewById<TextView>(R.id.settings_share).setOnClickListener {
            val messageToShare = viewModel.getShareLink()
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, messageToShare)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    private fun setupSupportButton() {
        findViewById<TextView>(R.id.settings_support).setOnClickListener {
            val supportData = viewModel.getSupportData()
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportData.email))
                putExtra(Intent.EXTRA_SUBJECT, supportData.subject)
                putExtra(Intent.EXTRA_TEXT, supportData.message)
            }
            startActivity(supportIntent)
        }
    }

    private fun setupUserAgreementButton() {
        findViewById<TextView>(R.id.settings_user_agreement).setOnClickListener {
            val agreementUrl = viewModel.getUserAgreementUrl()
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(agreementIntent)
        }
    }



}
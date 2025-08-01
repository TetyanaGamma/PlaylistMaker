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
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
  //  private lateinit var themeSwitch: SwitchCompat
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(
                Creator.provideSettingsInteractor(this),
                Creator.provideSharingInteractor(this)
            )
        )[SettingsViewModel::class.java]



        //обрабатываем нажатие на стрелку назад и возвращаемся на главный экран
        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }


        setupThemeObserver()
        setupThemeSwitch()
        setupShareButton()
        setupSupportButton()
        setupUserAgreementButton()

    }

    private fun setupThemeObserver() {
        viewModel.observe().observe(this) { isDarkTheme ->
            binding.settingsSwitchTheme.isChecked = isDarkTheme
            (application as App).switchTheme(isDarkTheme)
        }
    }

    private fun setupThemeSwitch() {
        binding.settingsSwitchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }
    }

    private fun setupShareButton() {
        binding.settingsShare.setOnClickListener {
            val messageToShare = viewModel.getShareLink()
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, messageToShare)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    private fun setupSupportButton() {
        binding.settingsSupport.setOnClickListener {
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
        binding.settingsUserAgreement.setOnClickListener {
            val agreementUrl = viewModel.getUserAgreementUrl()
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(agreementIntent)
        }
    }



}
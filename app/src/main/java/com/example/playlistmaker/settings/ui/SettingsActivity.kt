package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.net.toUri


class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                data = "mailto:".toUri()
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
            val agreementIntent = Intent(Intent.ACTION_VIEW, agreementUrl.toUri())
            startActivity(agreementIntent)
        }
    }

}
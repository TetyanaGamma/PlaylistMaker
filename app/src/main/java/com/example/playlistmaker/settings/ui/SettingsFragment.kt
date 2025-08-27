package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupThemeObserver()
        setupThemeSwitch()
        setupShareButton()
        setupSupportButton()
        setupUserAgreementButton()
    }

    private fun setupThemeObserver() {
        viewModel.observe().observe(viewLifecycleOwner) { isDarkTheme ->
            binding.settingsSwitchTheme.isChecked = isDarkTheme
            (requireActivity().application as App).switchTheme(isDarkTheme)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.settings_toolbar)
        //обрабатываем нажатие на стрелку назад и возвращаемся на главный экран
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareMaterialTextView: TextView = findViewById(R.id.settings_share)
        // обрабатываем нажатие на кнопку поделиться
        shareMaterialTextView.setOnClickListener {
            shareText()
        }

        val supportMaterialTextView: TextView = findViewById(R.id.settings_support)
        //обрабатываем нажатие на кнопку поддержка
        supportMaterialTextView.setOnClickListener {
            messageToSupport()
        }

        val agreementMaterialTextView: TextView = findViewById(R.id.settings_user_agreement)
        //обрабатываем нажатие на кнопку пользовательского соглашения
        agreementMaterialTextView.setOnClickListener {
            goToUserAgreement()
        }
    }

    fun shareText() {
        val messageToShare = getString(R.string.massage_to_share)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, messageToShare)
        shareIntent.type = "text/plain"
        val sendIntent = Intent.createChooser(shareIntent, null)
        startActivity(sendIntent)
    }

    fun messageToSupport() {
        val email = getString(R.string.my_email)
        val emailSubject = getString(R.string.email_subject)
        val emailBody = getString(R.string.email_body)
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, emailBody)
        startActivity(supportIntent)
    }

    fun goToUserAgreement() {
        val agreementUrl = getString(R.string.user_agreement_url)
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse("$agreementUrl")
        startActivity(agreementIntent)
    }

}
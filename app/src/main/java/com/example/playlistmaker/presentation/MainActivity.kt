package com.example.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.presentation.MediatekaActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.SearchActivity
import com.example.playlistmaker.presentation.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        //обрабока нажатия на кнопку поиск через лямбду для перехода на экран поиска
        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val mediatekaButton = findViewById<Button>(R.id.library_button)
        // обрабока нажатия на кнопку медиатека через лямбду для перехода на экран библиотеки
        mediatekaButton.setOnClickListener {
            val mediatekaIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(mediatekaIntent)
        }
        // обрабока нажатия на кнопку настройки через лямбду для перехода на экран настройки
        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}
package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediatekaBinding
import com.example.playlistmaker.mediateca.ui.MediatekaViewModel
import com.example.playlistmaker.mediateca.ui.MediatekaViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediatekaActivity : AppCompatActivity() {

    private var _binding: ActivityMediatekaBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabMediator: TabLayoutMediator
    private val adapter by lazy { MediatekaViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBackPressHandler()
        setupViewPager()
    }

    // Инициализация Toolbar и обработка нажатия кнопки "назад"
    private fun setupToolbar() {
        binding.toolBarMedia.setNavigationOnClickListener {
            finish()
        }
    }

    // Обработка нажатия кнопки "назад"
    private fun setupBackPressHandler() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    // Инициализация ViewPager и TabLayout
    private fun setupViewPager() {
        binding.viewPager.adapter = adapter

        // Инициализируем TabLayout
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorite_tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.adapter = null
        tabMediator.detach()
        _binding = null
    }
}
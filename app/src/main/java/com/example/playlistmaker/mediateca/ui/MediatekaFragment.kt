package com.example.playlistmaker.mediateca.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediatekaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediatekaFragment : Fragment() {

    private var _binding: FragmentMediatekaBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabMediator: TabLayoutMediator
    private var adapter: MediatekaViewPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediatekaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MediatekaViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        setupViewPager()
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        adapter = null
        tabMediator.detach()
        _binding = null
    }
}
package com.example.playlistmaker.mediateca.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediatekaBinding
import com.example.playlistmaker.mediateca.ui.adapters.MediatekaViewPagerAdapter
import com.example.playlistmaker.player.ui.screens.AudioplayerFragment
import com.example.playlistmaker.search.domain.model.Track
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediatekaFragment : Fragment(R.layout.fragment_mediateka),
    FavoriteTracksFragment.OnTrackClickListener {

    private var _binding: FragmentMediatekaBinding? = null
    private val binding get() = _binding!!
    private val mediaViewModel: MediatekaViewModel by viewModel()
    private lateinit var tabMediator: TabLayoutMediator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMediatekaBinding.bind(view)

        setupViewPager()
        observeTabSelection()
    }

    private fun setupViewPager() {
        val adapter = MediatekaViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        // Важное: отключаем автоматическое сохранение состояния
        binding.viewPager.isSaveEnabled = false

        // Восстанавливаем выбранный таб
        binding.viewPager.setCurrentItem(mediaViewModel.selectedTab.value, false)

        // Обновляем ViewModel при смене таба
        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mediaViewModel.selectTab(position)
            }
        })

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorite_tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }
        tabMediator.attach()
    }

    private fun observeTabSelection() {
        mediaViewModel.selectedTab
            .onEach { position ->
                if (binding.viewPager.currentItem != position) {
                    binding.viewPager.setCurrentItem(position, false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    // Делегируем переход на плеер из дочернего фрагмента
    override fun openPlayer(track: Track) {
        val bundle = Bundle().apply {
            putParcelable(AudioplayerFragment.Companion.TRACK_EXTRA, track)
        }
        // Используем Action из nav_graph
        findNavController().navigate(
            R.id.action_mediateka_to_audioplayer,
            bundle
        )
    }

    fun openPlaylistCreation() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val navController = findNavController()
            val currentDestination = navController.currentDestination?.id

            if (currentDestination == R.id.mediatekaFragment) {
                navController.navigate(
                    R.id.action_mediateka_to_playlistCreation,
                    null,
                    NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::tabMediator.isInitialized) tabMediator.detach()
        _binding = null
    }
}
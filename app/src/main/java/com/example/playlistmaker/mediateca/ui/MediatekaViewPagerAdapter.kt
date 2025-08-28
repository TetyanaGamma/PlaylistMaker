package com.example.playlistmaker.mediateca.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.media.ui.FavoriteTracksFragment
import com.example.playlistmaker.media.ui.PlaylistsFragment


class MediatekaViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val tabs = listOf(
        { FavoriteTracksFragment.newInstance() },
        { PlaylistsFragment.newInstance() }
    )

    override fun getItemCount(): Int = tabs.size // Количество вкладок

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment.newInstance()
            1 -> PlaylistsFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}
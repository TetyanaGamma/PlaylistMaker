package com.example.playlistmaker.mediateca.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.media.ui.FavoriteTracksFragment
import com.example.playlistmaker.media.ui.PlaylistsFragment

const val TAB_NUMBER = 2

class MediatekaViewPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = TAB_NUMBER // Количество вкладок

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment.newInstance()
            1 -> PlaylistsFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}
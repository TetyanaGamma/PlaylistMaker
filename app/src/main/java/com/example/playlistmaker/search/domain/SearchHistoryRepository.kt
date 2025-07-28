package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}


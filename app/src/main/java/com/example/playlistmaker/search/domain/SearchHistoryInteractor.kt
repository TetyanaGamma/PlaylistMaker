package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track

interface SearchHistoryInteractor  {
    fun getHistory(consumer: TrackConsumer)
    fun addTrack(track: Track)
    fun clearHistory()

    interface TrackConsumer {
        fun consume(tracks: List<Track>)
    }
}
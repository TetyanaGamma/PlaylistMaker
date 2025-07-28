package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun comsume(foundTracks: List<Track>)
    }
}
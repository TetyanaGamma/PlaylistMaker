package com.example.playlistmaker.search.domain

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
    }
    fun getHistory(): List<Track>
    fun saveTrackToHistory(track: Track)
    fun clearHistory()
    fun addTrack(track: Track)
}

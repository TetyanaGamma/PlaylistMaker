package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun getHistory(): Flow<Resource<List<Track>>>
    fun saveTrackToHistory(track: Track)
    fun clearHistory()
    fun addTrack(track: Track)
}

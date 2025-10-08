package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getHistory(): Flow<Resource<List<Track>>>
    fun addTrack(track: Track)
    fun clearHistory()
}
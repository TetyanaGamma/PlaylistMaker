package com.example.playlistmaker.search.domain.interactor


import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val historyRepository: SearchHistoryRepository
) : SearchInteractor {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> {
        return tracksRepository.searchTracks(expression)
    }

    override fun getHistory(): Flow<Resource<List<Track>>> {
        return historyRepository.getHistory()
    }

    override fun saveTrackToHistory(track: Track) {
        historyRepository.addTrack(track)
    }

    override fun clearHistory() {
        historyRepository.clearHistory()
    }

    override fun addTrack(track: Track) {
        historyRepository.addTrack(track)
    }
}

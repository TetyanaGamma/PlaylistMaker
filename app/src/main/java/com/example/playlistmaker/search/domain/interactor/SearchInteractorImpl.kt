package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val historyRepository: SearchHistoryRepository
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: SearchInteractor.TrackConsumer
    ) {
        executor.execute {
            try {
                val result = tracksRepository.searchTracks(expression)
                consumer.consume(result)
            } catch (e: Exception) {
                consumer.consume(null)
            }
        }
    }

    override fun getHistory(): List<Track> {
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
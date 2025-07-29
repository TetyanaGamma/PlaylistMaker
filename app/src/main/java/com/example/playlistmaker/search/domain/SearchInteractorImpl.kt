package com.example.playlistmaker.search.domain

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
            consumer.consume(tracksRepository.searchTracks(expression))
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

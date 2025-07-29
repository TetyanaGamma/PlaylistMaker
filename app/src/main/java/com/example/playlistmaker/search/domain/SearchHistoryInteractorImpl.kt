package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track

import java.util.concurrent.Executors

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun getHistory(consumer: SearchHistoryInteractor.TrackConsumer) {
        executor.execute {
            val history = searchHistoryRepository.getHistory()
            consumer.consume(history)
        }
    }

    override fun addTrack(track: Track) {
        searchHistoryRepository.addTrack(track)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }
}

package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository)
    : SearchHistoryInteractor{

    override fun getHistory(): List<Track> {
       return searchHistoryRepository.getHistory()
    }

    override fun addTrack(track: Track) {
     return   searchHistoryRepository.addTrack(track)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }


}
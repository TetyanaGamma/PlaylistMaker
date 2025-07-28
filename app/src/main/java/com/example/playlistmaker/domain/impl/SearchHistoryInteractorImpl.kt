package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.model.Track

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
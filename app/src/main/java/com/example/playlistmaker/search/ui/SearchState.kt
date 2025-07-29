package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.Track

sealed interface SearchState {
    object Loading : SearchState

    data class Content(val tracks: List<Track>) : SearchState
    data class History(val tracks: List<Track>) : SearchState


    object NoConnection : SearchState
    object NothingFound : SearchState
}

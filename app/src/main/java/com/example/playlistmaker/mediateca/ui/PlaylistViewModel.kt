package com.example.playlistmaker.mediateca.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.mediateca.domain.model.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    init {
        interactor.getAllPlaylists()
            .onEach { _playlists.value = it }
            .launchIn(viewModelScope)
    }

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.createPlaylist(playlist)
        }
    }

}
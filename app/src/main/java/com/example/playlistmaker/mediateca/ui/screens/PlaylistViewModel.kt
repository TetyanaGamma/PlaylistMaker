package com.example.playlistmaker.mediateca.ui.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.mediateca.domain.model.Playlist
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    // Список плейлистов
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        viewModelScope.launch {
            interactor.getAllPlaylists().collect { list ->
                _playlists.postValue(list)
            }
        }
    }


    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.createPlaylist(playlist)
        }
    }

}
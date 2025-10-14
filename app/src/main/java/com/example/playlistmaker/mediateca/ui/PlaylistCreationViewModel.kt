package com.example.playlistmaker.mediateca.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor

import com.example.playlistmaker.mediateca.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistCreationViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    fun createPlaylist(name: String, description: String, coverUrl: String) {
        val playlist = Playlist(
            playlistName = name,
            playlistDescr = description,
            playlistCoverUrl = coverUrl
        )

        viewModelScope.launch {
            interactor.createPlaylist(playlist)
            Log.d("PlaylistCreation", "Saved: $playlist")
        }
    }
}
